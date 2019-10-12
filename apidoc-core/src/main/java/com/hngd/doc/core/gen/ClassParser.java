package com.hngd.doc.core.gen;

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

import com.hngd.api.http.HttpInterfaceInfo;
import com.hngd.api.http.HttpParameterInfo;
import com.hngd.constant.HttpParameterType;
import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.parse.CommonClassCommentParser;
import com.hngd.doc.core.util.ClassUtils;
import com.hngd.doc.core.util.RestClassUtils;
import com.hngd.doc.core.util.SpringAnnotationUtils;
import com.hngd.doc.core.util.TypeUtils;


public class ClassParser {

	private static final Logger logger=LoggerFactory.getLogger(ClassParser.class);
	
	public static ModuleInfo processClass(Class<?> cls) {
		logger.info("start to process class:{}",cls.getName());
		if (!SpringAnnotationUtils.isControllerClass(cls)) {
			logger.info("There is no annotation Controller RestController for class:{}",cls.getName());
			return null;
		}
		ModuleInfo mi = new ModuleInfo();
		RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
		mi.moduleUrl=SpringAnnotationUtils.extractUrl(requestMapping);
		mi.moduleName = cls.getSimpleName();
		mi.simpleClassName = cls.getSimpleName();
		mi.deprecated=isModuleDeprecated(cls);
		Method[] methods = cls.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if(RestClassUtils.isHttpInterface(method)){
				HttpInterfaceInfo info = processMethod(method);
				if (info != null) {
					mi.interfaceInfos.add(info);
				}
			}else{
				logger.debug("method:{} has no http request annotation",cls.getName()+"."+method.getName());
			}
		}
		return mi;
	}
	
	private static Boolean isModuleDeprecated(Class<?> cls) {
		return cls.getAnnotation(Deprecated.class)!=null;
	}

	private static HttpInterfaceInfo processMethod(Method method) {
		HttpInterfaceInfo info = new HttpInterfaceInfo();
		Optional<? extends Annotation> optionalAnnotation = RestClassUtils.getHttpRequestInfo(method);
		Annotation mappingAnnotation=optionalAnnotation.get();
		info.deprecated=isMethodDeprecated(method);
		//extract consumes
        List<String> consumes=RestClassUtils.extractCosumes(mappingAnnotation);
		if (consumes != null) {
			info.consumes = consumes;
		}
		//extract produces
		List<String> produces=RestClassUtils.extractProduces(mappingAnnotation);
		if (produces != null) {
			info.produces = produces;
		}else {
			
		}
        info.methodUrl =RestClassUtils.extractUrl(mappingAnnotation);
		if(mappingAnnotation instanceof RequestMapping){
			RequestMethod[] methods=((RequestMapping)mappingAnnotation).method();
			if(methods==null || methods.length==0) {
				String methodKey=ClassUtils.getMethodIdentifier(method);
				logger.error("The RequestMapping annotation for method:{} has a empty method value",methodKey);
			    throw new RuntimeException("方法"+methodKey+"的RequestMapping注解缺少method值");
			}else {
				info.httpMethod = methods[0].name();
			}
		}else{
			info.httpMethod = mappingAnnotation.annotationType().getSimpleName().replace("Mapping", "");
		}
		info.retureTypeName = method.getReturnType().getSimpleName();
		info.retureType = method.getReturnType();
		info.methodName = method.getName();
		info.retureType = method.getGenericReturnType();
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			List<HttpParameterInfo> hpi=processParameter(parameter);
			if(!CollectionUtils.isEmpty(hpi)) {
				info.parameterInfos.addAll(hpi);
				HttpParameterInfo firstParameterInfo=hpi.get(0);
				if (!info.isMultipart) {
					info.isMultipart = TypeUtils.isMultipartType(firstParameterInfo.getParamJavaType());
				}
				info.hasRequestBody=!firstParameterInfo.getParamType().isParameter();
			}else {
				String parameterKey=ClassUtils.getParameterIdentifier(parameter);
				logger.error("the http parameters extracted from {} is empty",parameterKey);
			}
			
		}
		return info;
	}
	private static boolean isMethodDeprecated(Method method) {
		return method.getAnnotation(Deprecated.class)!=null;
	}

	/**
	 * @see <a href="https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/web.html#mvc-ann-arguments">Spring MVC Method Arguments</a> 
	 * @param parameter
	 * @return
	 */
	private static List<HttpParameterInfo> processParameter(Parameter parameter) {
		Annotation[] annotations = parameter.getAnnotations();
		HttpParameterInfo rpi=null;
		if (isRequestParam(annotations).isPresent()) {
			RequestParam requestParam =isRequestParam(annotations).get();
			rpi = new HttpParameterInfo();
			rpi.name = RestClassUtils.extractParameterName(requestParam);
			rpi.paramType = HttpParameterType.query;
			rpi.required = requestParam.required();
			rpi.paramJavaType=parameter.getParameterizedType();
			Optional<String> dateFormat=extractDataFormat(annotations);
			if(dateFormat.isPresent()) {
				rpi.format=dateFormat.get();
			}
			rpi.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			return Arrays.asList(rpi);
		}else if(isPathVariable(annotations).isPresent()) {
			PathVariable pa =isPathVariable(annotations).get();
			rpi = new HttpParameterInfo();
			rpi.name =RestClassUtils.extractParameterName(pa);
			rpi.paramType = HttpParameterType.path;
			rpi.required = true;
			rpi.paramJavaType=parameter.getParameterizedType();
			Optional<String> dateFormat=extractDataFormat(annotations);
			if(dateFormat.isPresent()) {
				rpi.format=dateFormat.get();
			}
			rpi.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			return Arrays.asList(rpi);
		}else if(isRequestBody(annotations).isPresent()) {
			//RequestBody rb= isRequestBody(annotations).get();
		    rpi = new HttpParameterInfo();
		    rpi.name = parameter.getName();
		    rpi.paramType = HttpParameterType.body;
		    rpi.required = true;
		    rpi.paramJavaType=parameter.getParameterizedType();
		    Optional<String> dateFormat=extractDataFormat(annotations);
		    if(dateFormat.isPresent()) {
			    rpi.format=dateFormat.get();
		    }
		    rpi.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
		    return Arrays.asList(rpi);
		}else if(isRequestPart(annotations).isPresent()) {
			 RequestPart requestPart =isRequestPart(annotations).get();
			 rpi = new HttpParameterInfo();
			 rpi.name = RestClassUtils.extractParameterName(requestPart);
			 rpi.paramType = HttpParameterType.body;
			 rpi.required = requestPart.required();
			 rpi.paramJavaType=parameter.getParameterizedType();
			 Optional<String> dateFormat=extractDataFormat(annotations);
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
					rpi = new HttpParameterInfo();
					rpi.name = parameter.getName();
					rpi.paramType = HttpParameterType.query;
					rpi.required = false;
					rpi.paramJavaType=parameter.getType();
					Optional<String> dateFormat=extractDataFormat(annotations);
					if(dateFormat.isPresent()) {
						rpi.format=dateFormat.get();
					}
					rpi.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			        return Arrays.asList(rpi);
				}else {
					//model
					return extractParametersFormModel(parameter);
					
				}
				
				
			}
			
		}
		
	}
	private static Optional<String> extractPropertyDataFormat(Class<?> clazz,PropertyDescriptor pd) {
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
			NotNull notNullConstraint=pd.getReadMethod().getAnnotation(NotNull.class);
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
	public static List<HttpParameterInfo> extractParametersFormModel(Parameter parameter){
		Class<?> clazz=parameter.getType();
		PropertyDescriptor[] pds=BeanUtils.getPropertyDescriptors(clazz);
		Field[] fields=clazz.getDeclaredFields();
		List<HttpParameterInfo> rpis=new LinkedList<>();
		for(PropertyDescriptor property:pds) {
			HttpParameterInfo rpi = new HttpParameterInfo();
			rpi.name = property.getName();
			Field field=ReflectionUtils.findField(clazz,rpi.name);
			if(field==null) {
				logger.warn("the property{} is read only",rpi.name);
				continue;
			}
			//TODO need to analysis method mapping url
			rpi.paramType = HttpParameterType.query;
			rpi.required = isPropertyRequired(clazz,property);
			rpi.paramJavaType=property.getPropertyType();
			rpi.comment=CommonClassCommentParser.getFieldComment(field);
			Optional<String> dateFormat=extractPropertyDataFormat(clazz,property);
			if(dateFormat.isPresent()) {
				rpi.format=dateFormat.get();
			}
			rpi.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			rpis.add(rpi);
		}
		return  rpis;
	}
	private static Optional<RequestPart> isRequestPart(Annotation[] annotations) {
		if(annotations.length<=0) {
			return Optional.empty();
		}
		for (Annotation a : annotations) {
			if(a instanceof RequestPart) {
				return Optional.of(((RequestPart)a));
			}
		}
		return Optional.empty();
	}

	private static Optional<String> extractDataFormat(Annotation[] annotations) {
		if(annotations.length<=0) {
			return Optional.empty();
		}
		for (Annotation a : annotations) {
			if(a instanceof DateTimeFormat) {
				return Optional.of(((DateTimeFormat)a).pattern());
			}
		}
		return Optional.empty();
	}

	private static boolean isFieldRequired(Field field) {
		return field.getAnnotation(NotNull.class)!=null;
	}
	private static Optional<RequestParam> isRequestParam(Annotation [] annotations) {
		return extractAnnotaions(annotations, RequestParam.class);
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Annotation> Optional<T> extractAnnotaions(Annotation [] annotations,Class<T> t){
		if(annotations.length<=0) {
			return Optional.empty();
		}
		for (Annotation a : annotations) {
			if(t.isInstance(a)) {
				return Optional.of((T)a);
			}
		}
		return Optional.empty();
	}
	
	private static Optional<RequestBody> isRequestBody(Annotation [] annotations) {
		return extractAnnotaions(annotations, RequestBody.class);
	}
	private static Optional<PathVariable> isPathVariable(Annotation [] annotations) {
		return extractAnnotaions(annotations, PathVariable.class);
	}
}
