/**
 * Copyright (c) 2015,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：TypeUtils.java
 * @时间：2016年8月28日 下午2:09:37
 * @作者：
 * @备注：
 * @版本:
 */

package com.hngd.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author
 */
public class TypeUtils {
    /**
     * @param parameterType
     * @return
     * @author
     * @since 0.0.1
     */
    public static boolean isPrimitiveType(Type parameterType) {
        if (!(parameterType instanceof Class<?>)) {
            return false;
        }
        if (String.class.equals(parameterType)) {
            return true;
        }
        Class<?> cls = (Class<?>) parameterType;
        if (Number.class.isAssignableFrom(cls)) {
            return true;
        }
        if (Boolean.class.isAssignableFrom(cls)) {
            return true;
        }
        return false;
    }

    public static boolean isMultipartType(Type parameterType) {
        if (!(parameterType instanceof Class<?>)) {
            return false;
        }

        if (MultipartFile.class.equals(parameterType)) {
            return true;
        }
        Class<?> type = (Class<?>) parameterType;
        // MultipartFile[]
        if (type.isArray() && MultipartFile.class.equals(type.getComponentType())) {
            return true;
        }
        return false;
    }
}
