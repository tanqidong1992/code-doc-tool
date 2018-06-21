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

package com.hngd.doc.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.mail.Multipart;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author
 */
public class TypeUtils
{
    /**
     * @param parameterType
     * @return
     * @author
     * @since 0.0.1
     */
    public static boolean isPrimitiveType(Type parameterType)
    {
        if (!(parameterType instanceof Class<?>))
        {
            return false;
        }
        if (String.class.equals(parameterType))
        {
            return true;
        }
        Class<?> cls = (Class<?>) parameterType;
        if (Number.class.isAssignableFrom(cls))
        {
            return true;
        }
        if (Boolean.class.isAssignableFrom(cls))
        {
            return true;
        }
        return false;
    }

    public static boolean isMultipartType(Type parameterType)
    {
        if (!(parameterType instanceof Class<?>))
        {
            return false;
        }
        if (MultipartFile.class.equals(parameterType))
        {
            return true;
        }
        return false;
    }

    public static String getTypeName(Type type)
    {
        if (type == null)
        {
            return null;
        }
        if (type instanceof ParameterizedType)
        {
            ParameterizedType pt = (ParameterizedType) type;
            Type rawType = pt.getRawType();
            Type ownerType = pt.getOwnerType();
            Type[] subTypes = pt.getActualTypeArguments();
            String rawTypeName = getTypeName(rawType);
            String ownerTypeName = getTypeName(ownerType);
            StringBuilder sb = new StringBuilder();
            if (subTypes != null)
            {
                for (Type subType : subTypes)
                {
                    String subTypeName = getTypeName(subType);
                    sb.append(subTypeName);
                    sb.append(",");
                }
            }
            String subTypesName = null;
            if (sb.length() > 0)
            {
                subTypesName = sb.substring(0, sb.length() - 1);
            }
            StringBuilder simpleTypeName = new StringBuilder();
            if (ownerTypeName != null)
            {
                simpleTypeName.append(ownerTypeName);
                simpleTypeName.append(".");
            }
            if (rawTypeName != null)
            {
                simpleTypeName.append(rawTypeName);
            }
            if (subTypesName != null)
            {
                simpleTypeName.append("<");
                simpleTypeName.append(subTypesName);
                simpleTypeName.append(">");
            }
            return simpleTypeName.toString();
        } else if (type instanceof Class)
        {
            Class<?> cls = (Class) type;
            return cls.getSimpleName();
        } else
        {
            return null;
        }
    }
}
