package com.hngd.doc.core.gen;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.hngd.api.http.HttpInterfaceInfo;
import com.hngd.api.http.HttpParameterInfo;
import com.hngd.api.http.HttpParameterInfo.HttpParameterType;
import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.util.RestClassUtils;
import com.hngd.doc.core.util.SpringAnnotationUtils;
import com.hngd.doc.core.util.TypeUtils;

import io.swagger.v3.core.util.ReflectionUtils;

public class ClassParser {

	private static final Logger logger=LoggerFactory.getLogger(ClassParser.class);
	
	public static ModuleInfo processClass(Class<?> cls) {
		logger.info("start to process class:{}",cls.getName());
		ModuleInfo mi = new ModuleInfo();
		Controller controller = cls.getAnnotation(Controller.class);
		RestController restController = cls.getAnnotation(RestController.class);
		if (controller == null && restController == null) {
			logger.warn("class:{} has no annotation {},{}",cls.getName(),Controller.class.getName(),RestController.class.getName());
			return null;
		}
		RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
		mi.moduleUrl=SpringAnnotationUtils.extractUrl(requestMapping);
		mi.moduleName = cls.getSimpleName();
		mi.simpleClassName = cls.getSimpleName();

		Method[] methods = cls.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if(RestClassUtils.isHttpInterface(method)){
				HttpInterfaceInfo info=null;
				try {
					info = processMethod(method);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					logger.error("", e);
				}
				if (info != null) {
					mi.interfaceInfos.add(info);
				}
			}else{
				logger.warn("method:{} has no http request annotation",cls.getName()+"."+method.getName());
			}
		}
		return mi;
	}
	
	private static HttpInterfaceInfo processMethod(Method method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HttpInterfaceInfo info = new HttpInterfaceInfo();
		Optional<? extends Annotation> optionalAnnotation = RestClassUtils.getHttpRequestInfo(method);
		Annotation mappingAnnotation=optionalAnnotation.get();
		//extract consumes
        Method consumesMethod=mappingAnnotation.getClass().getDeclaredMethod("consumes");
		String[] consumes = (String[]) consumesMethod.invoke(mappingAnnotation);
		if (consumes != null && consumes.length > 0) {
			info.consumes = new ArrayList<>(Arrays.asList(consumes));
		}
		//extract produces
		Method producesMethod=mappingAnnotation.getClass().getDeclaredMethod("produces");
		String[] produces = (String[]) producesMethod.invoke(mappingAnnotation);
		if (produces != null && produces.length > 0) {
			info.produces = new ArrayList<>(Arrays.asList(produces));
		}
        info.methodUrl =RestClassUtils.extractUrl(mappingAnnotation);
		if(mappingAnnotation instanceof RequestMapping){
			info.httpMethod = ((RequestMapping)mappingAnnotation).method()[0].name();
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
			if(hpi!=null) {
				info.parameterInfos.addAll(hpi);
				if (!info.isMultipart) {
					info.isMultipart = TypeUtils.isMultipartType(hpi.get(0).getParamJavaType());
				}
			}
			
		}
		return info;
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
			rpi.name = requestParam.value();
			rpi.paramType = HttpParameterType.query;
			rpi.required = requestParam.required();
			rpi.paramJavaType=parameter.getParameterizedType();
			Optional<String> dateFormat=extractDataFormat(annotations);
			if(dateFormat.isPresent()) {
				rpi.format=dateFormat.get();
			}
			rpi.isArgumentTypePrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			return Arrays.asList(rpi);
		}else if(isPathVariable(annotations).isPresent()) {
			PathVariable pa =isPathVariable(annotations).get();
			rpi = new HttpParameterInfo();
			rpi.name = pa.value();
			rpi.paramType = HttpParameterType.path;
			rpi.required = true;
			rpi.paramJavaType=parameter.getParameterizedType();
			Optional<String> dateFormat=extractDataFormat(annotations);
			if(dateFormat.isPresent()) {
				rpi.format=dateFormat.get();
			}
			rpi.isArgumentTypePrimitive=BeanUtils.isSimpleProperty(parameter.getType());
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
					rpi.isArgumentTypePrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			        return Arrays.asList(rpi);
				}else {
					//model
					Class<?> clazz=parameter.getType();
					Field[] fields=clazz.getDeclaredFields();
					List<HttpParameterInfo> rpis=new LinkedList<>();
					for(Field field:fields) {
						rpi = new HttpParameterInfo();
						rpi.name = field.getName();
						//TODO need to analysis method mapping url
						rpi.paramType = HttpParameterType.query;
						rpi.required = isFieldRequired(field);
						rpi.paramJavaType=field.getType();
						Optional<String> dateFormat=extractDataFormat(field.getAnnotations());
						if(dateFormat.isPresent()) {
							rpi.format=dateFormat.get();
						}
						rpi.isArgumentTypePrimitive=BeanUtils.isSimpleProperty(parameter.getType());
						rpis.add(rpi);
					}
					return  rpis;
				}
				
				
			}
			
		}
		
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
		if(annotations.length<=0) {
			return Optional.empty();
		}
		for (Annotation a : annotations) {
			if(a instanceof RequestParam) {
				return Optional.of((RequestParam)a);
			}
		}
		return Optional.empty();
	}
	
	private static Optional<PathVariable> isPathVariable(Annotation [] annotations) {
		if(annotations.length<=0) {
			return Optional.empty();
		}
		for (Annotation a : annotations) {
			if(a instanceof PathVariable) {
				return Optional.of((PathVariable)a);
			}
		}
		return Optional.empty();
	}
}
