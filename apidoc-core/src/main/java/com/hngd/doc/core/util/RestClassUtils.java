package com.hngd.doc.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class RestClassUtils {

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
}
