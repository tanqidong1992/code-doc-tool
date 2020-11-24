package com.hngd.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class RestClassUtils {

	private static final Logger logger=LoggerFactory.getLogger(RestClassUtils.class);
	public static final List<Class<? extends Annotation>> httpInterfaceAnnotationClazzs = Arrays.asList(
			RequestMapping.class, 
			PostMapping.class,
			GetMapping.class,
			DeleteMapping.class,
			PatchMapping.class,
			PutMapping.class
	);

	public static Optional<? extends Annotation> getHttpRequestInfo(Method method) {
		return httpInterfaceAnnotationClazzs.stream()
				.filter(clazz -> method.getAnnotation(clazz) != null)
				.map(clazz -> method.getAnnotation(clazz))
				.findAny();

	}

	public static boolean isHttpInterface(Method method) {
		return getHttpRequestInfo(method).isPresent();
	}
	
	public static String HTTP_PATH_DELIMITER="/";
	/**
	 * 从 Mapping中提取出请求url，req为null则返回"/"
	 * @param req，
	 * @return
	 */
	public static String extractUrl(Annotation req) {
		
		if(req==null) {
			return HTTP_PATH_DELIMITER;
		}
		String url=null;
		Method valueMethod=null;
		Object valueObject=null;
		try {
			valueMethod = req.getClass().getDeclaredMethod("value");
			valueObject=valueMethod.invoke(req);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("",e);
		}
		if(valueObject instanceof String[]){
			String[] values=(String[])valueObject;
			if(values.length>0) {
				url =values[0];
			}
		}
		if(url==null){
			Method pathMethod = null;
			try {
				pathMethod = req.getClass().getDeclaredMethod("path");
				valueObject = pathMethod.invoke(req);
				if(valueObject instanceof String[]){
					String[] values=(String[])valueObject;
					if(values.length>0) {
						url =values[0];
					}
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error("",e);
			}
		}
		if(url==null || "".equals(url)) {
		    return HTTP_PATH_DELIMITER;
		}else {
			if(!StringUtils.startsWith(url, HTTP_PATH_DELIMITER)) {
				url = HTTP_PATH_DELIMITER + url;
			}
			return url;
		}
	}
	
	/**
	 * 从Mapping中提取出请求方法
	 * @param req，
	 * @return
	 */
	public static Optional<String> extractHttpMethod(Annotation req) {
		String method=null;
		if(req instanceof RequestMapping){
			RequestMethod[] methods=((RequestMapping)req).method();
			if(methods==null || methods.length==0) {
				 return Optional.empty();
			}else {
				//TODO one java method only has one http method?
				method = methods[0].name();
			}
		}else{
			method = req.annotationType().getSimpleName().replace("Mapping", "");
		}
		return Optional.ofNullable(method);
	}
	
	public static List<String> extractCosumes(Annotation req){
		String[] consumes=null;
		Method method=getAnnotationMethod(req, "consumes");
		try {
			consumes = (String[]) method.invoke(req);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("",e);
		}
		if (consumes != null && consumes.length > 0) {
		    return new ArrayList<>(Arrays.asList(consumes));
		}else {
			return null;
		}
	}
	
	public static List<String> extractProduces(Annotation req){
		String[] produces=null;
		Method method=getAnnotationMethod(req, "produces");
		try {
			produces = (String[]) method.invoke(req);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("",e);
		}
		if (produces != null && produces.length > 0) {
		    return new ArrayList<>(Arrays.asList(produces));
		}else {
			return null;
		}
	}
	
	private static Method getAnnotationMethod(Annotation a, String name) {
		Method m = null;
		try {
			m = a.getClass().getDeclaredMethod(name);
		} catch (NoSuchMethodException | SecurityException e) {
			logger.error("", e);
		}
		return m;
	}

    public static String extractParameterName(Annotation req) {
		if(req==null) {
			return "";
		}
		String name=null;
		Method valueMethod=null;
		Object valueObject=null;
		try {
			valueMethod = req.getClass().getDeclaredMethod("value");
			valueObject=valueMethod.invoke(req);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("",e);
		}
		if(valueObject!=null){
			name =(String)valueObject;
		}else{
			Method nameMethod = null;
			try {
				nameMethod = req.getClass().getDeclaredMethod("name");
				name = (String) nameMethod.invoke(req);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error("",e);
			}
		}
		if(name==null) {
		    return "";
		}else {
			return name;
		}
	}
}
