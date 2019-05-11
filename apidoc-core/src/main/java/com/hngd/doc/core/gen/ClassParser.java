package com.hngd.doc.core.gen;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.api.http.HttpInterfaceInfo;
import com.hngd.api.http.HttpParameterInfo;
import com.hngd.api.http.HttpParameterInfo.HttpParameterType;
import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.util.RestClassUtils;
import com.hngd.doc.core.util.TypeUtils;

public class ClassParser {

	private static final Logger logger=LoggerFactory.getLogger(ClassParser.class);
	
	public static ModuleInfo processClass(Class<?> cls) {
		ModuleInfo mi = new ModuleInfo();
		Controller controller = cls.getAnnotation(Controller.class);
		RestController restController = cls.getAnnotation(RestController.class);
		if (controller == null && restController == null) {
			 
			logger.error("class:{} has no annotation {},{}",cls.getName(),Controller.class.getName(),RestController.class.getName());
			return null;
		}
		RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
		if (requestMapping == null || requestMapping.value().length <= 0) {
			logger.warn("class[" + cls.getName() + "] has no annotation[" + RequestMapping.class.getName() + "]");
			return null;
		}
		mi.moduleUrl = requestMapping.value()[0];
		if (!StringUtils.startsWith(mi.moduleUrl, "/")) {
			mi.moduleUrl = "/" + mi.moduleUrl;
		}
		mi.moduleName = mi.moduleUrl.substring(1);
		mi.simpleClassName = cls.getSimpleName();
		RequestMethod[] requestMethods = requestMapping.method();
		RequestMethod requestMethod = null;
		if (requestMethods.length > 0) {
			requestMethod = requestMethods[0];
		}
		// TODO Auto-generated method stub
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
				
			 
				logger.warn("method:{} has no http request annotation",method.getName());
			}
		}
		return mi;
	}
	
	private static HttpInterfaceInfo processMethod(Method method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HttpInterfaceInfo info = new HttpInterfaceInfo();
		
		Optional<? extends Annotation> annotation = RestClassUtils.getHttpRequestInfo(method);
		Annotation a=annotation.get();
        Method consumesMethod=a.getClass().getDeclaredMethod("consumes");
		String[] consumes = (String[]) consumesMethod.invoke(a);
		if (consumes != null && consumes.length > 0) {
			info.consumes = new ArrayList<>(Arrays.asList(consumes));
		}
		
		 Method producesMethod=a.getClass().getDeclaredMethod("produces");
		String[] produces = (String[]) producesMethod.invoke(a);
		if (produces != null && produces.length > 0) {
			info.produces = new ArrayList<>(Arrays.asList(produces));
		}
		Method valueMethod=a.getClass().getDeclaredMethod("value");
		Object value=valueMethod.invoke(a);
		if(value!=null){
			info.methodUrl =((String[])value)[0];
		}else{
			Method pathMethod=a.getClass().getDeclaredMethod("path");
			String[] path=(String[]) pathMethod.invoke(a);
			info.methodUrl =path[0];
		}
		 
		if (!StringUtils.startsWith(info.methodUrl, "/")) {
			info.methodUrl = "/" + info.methodUrl;
		}
		
		if(a instanceof RequestMapping){
			info.httpMethod = ((RequestMapping)a).method()[0].name();
		}else{
			info.httpMethod = a.annotationType().getSimpleName().replace("Mapping", "");
		}
		
		info.retureTypeName = method.getReturnType().getSimpleName();
		info.retureType = method.getReturnType();
		info.methodName = method.getName();
		info.retureType = method.getGenericReturnType();
		
		Annotation[][] annotationss = method.getParameterAnnotations();
		Parameter[] parameters = method.getParameters();
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			// System.out.println(parameterTypes[i].getName());
			Parameter parameter = parameters[i];
			Annotation[] annotations = parameter.getAnnotations();
			if (annotations.length > 0) {
				for (Annotation a1 : annotations) {
					if (a1 instanceof RequestParam) {
						RequestParam requestParam = (RequestParam) a1;
						HttpParameterInfo rpi = new HttpParameterInfo();
						rpi.name = requestParam.value();
						rpi.paramType = HttpParameterType.query;
						rpi.required = requestParam.required();
						info.parameterInfos.add(rpi);
						info.parameterTypes.add(parameter.getParameterizedType());
						if (!info.isMultipart) {
							info.isMultipart = TypeUtils.isMultipartType(parameter.getParameterizedType());
						}
						break;
					} else if (a1 instanceof PathVariable) {
						PathVariable pa = (PathVariable) a1;
						HttpParameterInfo rpi = new HttpParameterInfo();
						rpi.name = pa.value();
						rpi.paramType = HttpParameterType.path;
						rpi.required = true;
						info.parameterInfos.add(rpi);
						if (!info.isMultipart) {
							info.isMultipart = TypeUtils.isMultipartType(parameter.getParameterizedType());
						}
						info.parameterTypes.add(parameter.getParameterizedType());
						break;
					}
				}
			}
		}
		return info;
	}
}
