package com.hngd.parser.clazz;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.hngd.constant.HttpParameterType;
import com.hngd.exception.ClassParseException;
import com.hngd.openapi.entity.HttpInterface;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.parser.entity.ModuleInfo;
import com.hngd.parser.source.CommonClassCommentParser;
import com.hngd.utils.ClassUtils;
import com.hngd.utils.RestClassUtils;
import com.hngd.utils.SpringAnnotationUtils;
import com.hngd.utils.TypeUtils;


public class ClassParser {

	private static final Logger logger=LoggerFactory.getLogger(ClassParser.class);
	public static ModuleInfo processClass(Class<?> cls) {
		ModuleInfo mi=null;
		try {
		    mi=doProcessClass(cls);
		}catch(Exception e) {
			if(e instanceof ClassParseException) {
				throw e;
			}else {
				throw new ClassParseException(cls,e);
			}
		}
		return mi;
	}
	public static ModuleInfo doProcessClass(Class<?> cls) {
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
			HttpInterface info = processMethod(method);
			if (info != null) {
			    mi.interfaceInfos.add(info);
			}
		}
		return mi;
	}
	

	private static HttpInterface processMethod(Method method) {
		HttpInterface hi=null;
		try {
		    hi=doProcessMethod(method);
		}catch(Exception e) {
			if(e instanceof ClassParseException) {
				throw e;
			}else {
				throw new ClassParseException(method.getDeclaringClass(),method,e);
			}
		}
		return hi;
	}
	private static HttpInterface doProcessMethod(Method method) {
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
		}else {
			
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
		HttpParameter rpi=null;
		if (isRequestParam(annotations).isPresent()) {
			RequestParam requestParam =isRequestParam(annotations).get();
			rpi = new HttpParameter();
			rpi.name = RestClassUtils.extractParameterName(requestParam);
			rpi.paramType = HttpParameterType.query;
			rpi.required = requestParam.required();
			rpi.paramJavaType=parameter.getParameterizedType();
			Optional<String> dateFormat=extractDateFormat(annotations);
			if(dateFormat.isPresent()) {
				rpi.format=dateFormat.get();
			}
			rpi.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			return Arrays.asList(rpi);
		}else if(isPathVariable(annotations).isPresent()) {
			PathVariable pa =isPathVariable(annotations).get();
			rpi = new HttpParameter();
			rpi.name =RestClassUtils.extractParameterName(pa);
			rpi.paramType = HttpParameterType.path;
			rpi.required = true;
			rpi.paramJavaType=parameter.getParameterizedType();
			Optional<String> dateFormat=extractDateFormat(annotations);
			if(dateFormat.isPresent()) {
				rpi.format=dateFormat.get();
			}
			rpi.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			return Arrays.asList(rpi);
		}else if(isRequestBody(annotations).isPresent()) {
			//RequestBody rb= isRequestBody(annotations).get();
		    rpi = new HttpParameter();
		    rpi.name = parameter.getName();
		    rpi.paramType = HttpParameterType.body;
		    rpi.required = true;
		    rpi.paramJavaType=parameter.getParameterizedType();
		    Optional<String> dateFormat=extractDateFormat(annotations);
		    if(dateFormat.isPresent()) {
			    rpi.format=dateFormat.get();
		    }
		    rpi.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
		    return Arrays.asList(rpi);
		}else if(isRequestPart(annotations).isPresent()) {
			 RequestPart requestPart =isRequestPart(annotations).get();
			 rpi = new HttpParameter();
			 rpi.name = RestClassUtils.extractParameterName(requestPart);
			 rpi.paramType = HttpParameterType.body;
			 rpi.required = requestPart.required();
			 rpi.paramJavaType=parameter.getParameterizedType();
			 Optional<String> dateFormat=extractDateFormat(annotations);
			 if(dateFormat.isPresent()) {
			     rpi.format=dateFormat.get();
			 }
			 rpi.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			 return Arrays.asList(rpi);
		}else {
			//TODO support all spring web parameters 
			//WebRequest req;
			//HttpMethod m;
			String typeName=parameter.getType().getName();
			if(typeName.startsWith("org.springframework")) {
				return null;
			}else if(typeName.startsWith("javax.servlet")) {
				return null;
			}else if(typeName.startsWith("java.io") || typeName.startsWith("java.security")){
				return null;
			}else {
				if(parameter.getType() instanceof Class && BeanUtils.isSimpleProperty((Class<?>)parameter.getType())) {
					//requestparam
					rpi = new HttpParameter();
					rpi.name = parameter.getName();
					rpi.paramType = HttpParameterType.query;
					rpi.required = false;
					rpi.paramJavaType=parameter.getType();
					Optional<String> dateFormat=extractDateFormat(annotations);
					if(dateFormat.isPresent()) {
						rpi.format=dateFormat.get();
					}
					rpi.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			        return Arrays.asList(rpi);
				}else {
					//model
					return extractParametersFromModel(parameter);
					
				}
			}
			
		}
		
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
	private static Optional<RequestPart> isRequestPart(Annotation[] annotations) {
		return extractAnnotaion(annotations, RequestPart.class);
	}
    /**
     * 提取DataFormatPattern
     * @param annotations
     * @return
     */
	private static Optional<String> extractDateFormat(Annotation[] annotations) {
		Optional<DateTimeFormat> optionalDateTimeFormat=extractAnnotaion(annotations, DateTimeFormat.class);
		if(optionalDateTimeFormat.isPresent()) {
		    return Optional.of(optionalDateTimeFormat.get().pattern());
		}
		return Optional.empty();
	}

	private static boolean isFieldRequired(Field field) {
		return field.getAnnotation(NotNull.class)!=null;
	}
	private static Optional<RequestParam> isRequestParam(Annotation [] annotations) {
		return extractAnnotaion(annotations, RequestParam.class);
	}
	/**
	 * 从注解数组中找到指定类型的注解
	 * @param <T> 指定注解
	 * @param annotations 注解数组
	 * @param type 指定注解的类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T extends Annotation> Optional<T> extractAnnotaion(Annotation [] annotations,Class<T> type){
		if(annotations.length<=0) {
			return Optional.empty();
		}
		for (Annotation a : annotations) {
			if(type.isInstance(a)) {
				return Optional.of((T)a);
			}
		}
		return Optional.empty();
	}
	
	private static Optional<RequestBody> isRequestBody(Annotation [] annotations) {
		return extractAnnotaion(annotations, RequestBody.class);
	}
	private static Optional<PathVariable> isPathVariable(Annotation [] annotations) {
		return extractAnnotaion(annotations, PathVariable.class);
	}
}
