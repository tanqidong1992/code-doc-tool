package com.hngd.utils;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionExtUtils {
    private static Logger logger=LoggerFactory.getLogger(ReflectionExtUtils.class);
	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			logger.error("Instantiate Class:"+clazz.getCanonicalName()+" failed!", e);
			
		}
		return null;
	}
}
