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

import javax.annotation.Nonnull;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author
 */
public class TypeUtils {
 
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
    /**
     * 
     * @param type
     * @deprecated replaced by toRef
     * @return
     */
    @Deprecated
    public static String toTypeName(Type type) {
        if(type==null){
            return null;
        }
        if(type instanceof ParameterizedType){
            ParameterizedType pt=(ParameterizedType) type;
            Type rawType=pt.getRawType();
            Type ownerType=pt.getOwnerType();
            Type[] subTypes=pt.getActualTypeArguments();
            String rawTypeName=toTypeName(rawType);
            String ownerTypeName=toTypeName(ownerType);
            StringBuilder sb=new StringBuilder();
            if(subTypes!=null){
                for(Type subType :subTypes){
                    String subTypeName=toTypeName(subType);
                    sb.append(subTypeName);
                    sb.append(",");
                }
            }
            String subTypesName=null;
            if(sb.length()>0){
                subTypesName=sb.substring(0, sb.length()-1);
            }
            StringBuilder simpleTypeName=new StringBuilder();
            if(ownerTypeName!=null){
                simpleTypeName.append(ownerTypeName);
                simpleTypeName.append(".");
            }
            if(rawTypeName!=null){
                simpleTypeName.append(rawTypeName);
            }
            if(subTypesName!=null){
                simpleTypeName.append("<");
                simpleTypeName.append(subTypesName);
                simpleTypeName.append(">");
            }
            return simpleTypeName.toString();
        }else if(type instanceof Class){
            Class<?> cls=(Class<?>)type;
            return cls.getSimpleName();
        }else{
            return null;
        }
    }
    
    public static String toRef(@Nonnull Type type) {
        if(type==null){
            return null;
        }
        if(type instanceof ParameterizedType){
            ParameterizedType pt=(ParameterizedType) type;
            Type rawType=pt.getRawType();
            Type[] subTypes=pt.getActualTypeArguments();
            String rawTypeName=toRef(rawType);
            StringBuilder ref=new StringBuilder();
            if(rawTypeName!=null){
                ref.append(rawTypeName);
            }
            
            if(subTypes!=null){
                StringBuilder sb=new StringBuilder();
                for(Type subType :subTypes){
                    String subTypeName=toRef(subType);
                    sb.append(subTypeName);
                }
                String subTypesName=sb.toString();
                ref.append(subTypesName);
            }
            return ref.toString();
        }else if(type instanceof Class){
            Class<?> cls=(Class<?>)type;
            return cls.getSimpleName();
        }else{
            return null;
        }
    }
}
