package com.hngd.parser.clazz;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.hngd.constant.HttpParameterType;
import com.hngd.exception.ClassParseException;
import com.hngd.openapi.entity.HttpInterface;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.parser.clazz.spring.MethodArgUtils;
import com.hngd.parser.clazz.spring.SpringAnnotationUtils;
import com.hngd.parser.entity.ModuleInfo;
import com.hngd.parser.source.CommonClassCommentParser;
import com.hngd.utils.ClassUtils;
import com.hngd.utils.RestClassUtils;
import com.hngd.utils.TypeUtils;


public class ClassParser {

	private static final Logger logger=LoggerFactory.getLogger(ClassParser.class);
	public static Optional<ModuleInfo> parseModule(Class<?> cls) {
		ModuleInfo mi=null;
		try {
		    mi=doConvertClassToModule(cls);
		}catch(Exception e) {
			if(e instanceof ClassParseException) {
				throw e;
			}else {
				throw new ClassParseException(cls,e);
			}
		}
		return Optional.of(mi);
	}
	private static ModuleInfo doConvertClassToModule(Class<?> cls) {
		logger.info("start to process class:{}",cls.getName());
		if (!SpringAnnotationUtils.isControllerClass(cls)) {
			logger.info("There is no annotation Controller or RestController for class:{}",cls.getName());
			return null;
		}
		ModuleInfo mi = new ModuleInfo();
		
		//parse module base info
		RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
		mi.moduleUrl=SpringAnnotationUtils.extractUrl(requestMapping);
		mi.moduleName = cls.getSimpleName();
		mi.simpleClassName = cls.getSimpleName();
		mi.canonicalClassName=cls.getName();
		mi.deprecated=ClassUtils.isClassDeprecated(cls);
		
		//parse module interfaces
		Method[] methods = cls.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if(!RestClassUtils.isHttpInterface(method)) {
				logger.debug("method:{}.{} has no http request annotation",
						cls.getName(),method.getName());
				continue;
			}
			Optional<HttpInterface> info = parseMethod(method);
			if (info.isPresent()) {
			    mi.interfaceInfos.add(info.get());
			}
		}
		return mi;
	}
	 
	private static Optional<HttpInterface> parseMethod(Method method) {
		HttpInterface hi=null;
		try {
		    hi=doParseMethod(method);
		}catch(Exception e) {
			if(e instanceof ClassParseException) {
				throw e;
			}else {
				throw new ClassParseException(method.getDeclaringClass(),method,e);
			}
		}
		return Optional.of(hi);
	}
	private static HttpInterface doParseMethod(Method method) {
		HttpInterface httpInterface = new HttpInterface();
		Optional<? extends Annotation> optionalAnnotation = RestClassUtils.getHttpRequestInfo(method);
		Annotation mappingAnnotation=optionalAnnotation.get();
		httpInterface.deprecated=isMethodDeprecated(method);
		//extract consumes
        List<String> consumes=RestClassUtils.extractCosumes(mappingAnnotation);
		if (consumes != null) {
			httpInterface.consumes = consumes;
		}
		//extract produces
		List<String> produces=RestClassUtils.extractProduces(mappingAnnotation);
		if (produces != null) {
			httpInterface.produces = produces;
		}
		//extract http url
        httpInterface.methodUrl =RestClassUtils.extractUrl(mappingAnnotation);
        //extract http method
		if(mappingAnnotation instanceof RequestMapping){
			RequestMethod[] methods=((RequestMapping)mappingAnnotation).method();
			if(methods==null || methods.length==0) {
				String methodKey=ClassUtils.getMethodIdentifier(method);
				logger.error("The RequestMapping annotation for method:{} has a empty method value",methodKey);
			    throw new RuntimeException("方法"+methodKey+"的RequestMapping注解缺少method值");
			}else {
				httpInterface.httpMethod = methods[0].name();
			}
		}else{
			httpInterface.httpMethod = mappingAnnotation.annotationType().getSimpleName().replace("Mapping", "");
		}
		httpInterface.retureTypeName = method.getReturnType().getSimpleName();
		httpInterface.retureType = method.getReturnType();
		httpInterface.methodName = method.getName();
		httpInterface.retureType = method.getGenericReturnType();
		//extract http parameters
		doProcessParamaters(method,httpInterface);
		
		return httpInterface;
	}
	private static void doProcessParamaters(Method method, HttpInterface httpInterface) {
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			List<HttpParameter> httpParams=processParameter(parameter);
			if(!CollectionUtils.isEmpty(httpParams)) {
				final int indexInJavaMethod=i;
				httpParams.stream()
				    .forEach(httpParam->httpParam.indexInJavaMethod=indexInJavaMethod);
				httpInterface.parameterInfos.addAll(httpParams);
				HttpParameter firstParameterInfo=httpParams.get(0);
				if (!httpInterface.isMultipart) {
					httpInterface.isMultipart = TypeUtils.isMultipartType(firstParameterInfo.getParamJavaType());
				}
				httpInterface.hasRequestBody=!firstParameterInfo.getParamType().isParameter();
			}else {
				String parameterKey=ClassUtils.getParameterIdentifier(parameter);
				logger.error("the http parameters extracted from {} is empty",parameterKey);
			}
			
		}
		
	}
	private static boolean isMethodDeprecated(Method method) {
		return method.getAnnotation(Deprecated.class)!=null;
	}

	/**
	 * @see <a href="https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/web.html#mvc-ann-arguments">Spring MVC Method Arguments</a> 
	 * @param parameter
	 * @param indexInJavaMethod 
	 * @return
	 */
	private static List<HttpParameter> processParameter(Parameter parameter) {
		Annotation[] annotations = parameter.getAnnotations();
		HttpParameter httpParam=new HttpParameter();
		Optional<RequestParam> optionalRequestParam=MethodArgUtils.isRequestParam(annotations);
		if (optionalRequestParam.isPresent()) {
			RequestParam requestParam =optionalRequestParam.get();
			httpParam.name = RestClassUtils.extractParameterName(requestParam);
			httpParam.paramType = HttpParameterType.query;
			httpParam.required = requestParam.required();
			httpParam.paramJavaType=parameter.getParameterizedType();
			Optional<String> dateFormat=MethodArgUtils.extractDateFormat(annotations);
			if(dateFormat.isPresent()) {
				httpParam.format=dateFormat.get();
			}
			httpParam.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			return Arrays.asList(httpParam);
		}
		
		Optional<PathVariable> optionalPathVariable=MethodArgUtils.isPathVariable(annotations);
		if(optionalPathVariable.isPresent()) {
			PathVariable pa =optionalPathVariable.get();
			httpParam.name =RestClassUtils.extractParameterName(pa);
			httpParam.paramType = HttpParameterType.path;
			httpParam.required = pa.required();
			httpParam.paramJavaType=parameter.getParameterizedType();
			Optional<String> dateFormat=MethodArgUtils.extractDateFormat(annotations);
			if(dateFormat.isPresent()) {
				httpParam.format=dateFormat.get();
			}
			httpParam.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			return Arrays.asList(httpParam);
		}
		
		Optional<RequestBody> optionalRequestBody=MethodArgUtils.isRequestBody(annotations);
		if(optionalRequestBody.isPresent()) {
			RequestBody rb= optionalRequestBody.get();
		    httpParam.name = parameter.getName();
		    httpParam.paramType = HttpParameterType.body;
		    httpParam.required = rb.required();
		    httpParam.paramJavaType=parameter.getParameterizedType();
		    Optional<String> dateFormat=MethodArgUtils.extractDateFormat(annotations);
		    if(dateFormat.isPresent()) {
			    httpParam.format=dateFormat.get();
		    }
		    httpParam.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
		    return Arrays.asList(httpParam);
		}
		
		Optional<RequestPart> optionalRequestPart=MethodArgUtils.isRequestPart(annotations);
		if(optionalRequestPart.isPresent()) {
			 RequestPart requestPart =optionalRequestPart.get();
			 httpParam.name = RestClassUtils.extractParameterName(requestPart);
			 httpParam.paramType = HttpParameterType.body;
			 httpParam.required = requestPart.required();
			 httpParam.paramJavaType=parameter.getParameterizedType();
			 Optional<String> dateFormat=MethodArgUtils.extractDateFormat(annotations);
			 if(dateFormat.isPresent()) {
			     httpParam.format=dateFormat.get();
			 }
			 httpParam.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			 return Arrays.asList(httpParam);
		}
		//TODO MatrixVariable is not suuported
		Optional<MatrixVariable> optionalMatrixVariable=MethodArgUtils.extractAnnotaion(annotations, MatrixVariable.class);
		if(optionalMatrixVariable.isPresent()) {
			ClassParseException.throwParameterParseException(parameter, "Unspported Matrix Variable", null);
		}
		Optional<RequestHeader> optionalRequestHeader=MethodArgUtils.extractAnnotaion(annotations, RequestHeader.class);
		if(optionalRequestHeader.isPresent()) {
			RequestHeader requestHeader =optionalRequestHeader.get();
			httpParam.name = RestClassUtils.extractParameterName(requestHeader);
			httpParam.paramType = HttpParameterType.header;
			httpParam.required = requestHeader.required();
			httpParam.paramJavaType=parameter.getParameterizedType();
			Optional<String> dateFormat=MethodArgUtils.extractDateFormat(annotations);
			if(dateFormat.isPresent()) {
				httpParam.format=dateFormat.get();
			}
			httpParam.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			return Arrays.asList(httpParam);
		}
		Optional<CookieValue> optionalCookieValue=MethodArgUtils.extractAnnotaion(annotations, CookieValue.class);
		if(optionalCookieValue.isPresent()) {
			CookieValue cv=optionalCookieValue.get();
			httpParam.name = RestClassUtils.extractParameterName(cv);
			httpParam.paramType = HttpParameterType.cookie;
			httpParam.required = cv.required();
			httpParam.paramJavaType=parameter.getParameterizedType();
			Optional<String> dateFormat=MethodArgUtils.extractDateFormat(annotations);
			if(dateFormat.isPresent()) {
				httpParam.format=dateFormat.get();
			}
			httpParam.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			return Arrays.asList(httpParam);
		}
        //如果是Spring自动注入的参数类型就直接返回
		Type paramType= parameter.getParameterizedType();
		if (MethodArgUtils.isSpringAutoInjectParameterType(paramType)) {
			
		    return null;
		}
		//For access to the model that is used in HTML controllers and exposed to templates as part of view rendering.
		if(MethodArgUtils.isModelParameterType(paramType)){
			return null;
		}
		if (parameter.getType() instanceof Class && BeanUtils.isSimpleProperty((Class<?>) parameter.getType())) {
			// requestparam
			httpParam = new HttpParameter();
			httpParam.name = parameter.getName();
			httpParam.paramType = HttpParameterType.query;
			httpParam.required = false;
			httpParam.paramJavaType = parameter.getType();
			Optional<String> dateFormat = MethodArgUtils.extractDateFormat(annotations);
			if (dateFormat.isPresent()) {
				httpParam.format = dateFormat.get();
			}
			httpParam.isPrimitive = BeanUtils.isSimpleProperty(parameter.getType());
			return Arrays.asList(httpParam);
		}else{
			//model
			return extractParametersFromModel(parameter);
		}
		//ClassParseException.throwParameterParseException(parameter, "Not Supported Parameter Type",null); 
		 
		 
	}
	private static Optional<String> extractPropertyDateFormat(Class<?> clazz,PropertyDescriptor pd) {
		String propertyName=pd.getName();
		Field field=ReflectionUtils.findField(clazz, propertyName);
		if(field==null) {
			logger.error("field for property:{} is not found",propertyName);
			return Optional.empty();
		}
		DateTimeFormat a=field.getAnnotation(DateTimeFormat.class);
		if(a!=null) {
			return Optional.ofNullable(a.pattern());
		}
		return Optional.empty();
	}
	private static boolean isPropertyRequired(Class<?> clazz,PropertyDescriptor pd) {
		Method readMethod=pd.getReadMethod();
		if(readMethod!=null) {
			NotNull notNullConstraint=readMethod.getAnnotation(NotNull.class);
			if(notNullConstraint!=null) {
				return true;
			}
		}
		String propertyName=pd.getName();
		Field field=ReflectionUtils.findField(clazz, propertyName);
		if(field!=null) {
			NotNull notNullConstraint=field.getAnnotation(NotNull.class);
			if(notNullConstraint!=null) {
				return true;
			}
		}
		return false;
	}
	public static List<HttpParameter> extractParametersFromModel(Parameter parameter){
		Class<?> clazz=parameter.getType();
		PropertyDescriptor[] propertyDescriptors=BeanUtils.getPropertyDescriptors(clazz);
		List<HttpParameter> httpParams=new LinkedList<>();
		for(PropertyDescriptor property:propertyDescriptors) {
			HttpParameter httpParam = new HttpParameter();
			httpParam.name = property.getName();
			Field field=ReflectionUtils.findField(clazz,httpParam.name);
			if(field==null) {
				logger.warn("the property {} is read only",httpParam.name);
				continue;
			}
			//TODO need to analysis method mapping url
			httpParam.paramType = HttpParameterType.query;
			httpParam.required = isPropertyRequired(clazz,property);
			httpParam.paramJavaType=property.getPropertyType();
			httpParam.comment=CommonClassCommentParser.getFieldComment(field);
			Optional<String> dateFormat=extractPropertyDateFormat(clazz,property);
			if(dateFormat.isPresent()) {
				httpParam.format=dateFormat.get();
			}
			httpParam.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			httpParams.add(httpParam);
		}
		return  httpParams;
	}

}
