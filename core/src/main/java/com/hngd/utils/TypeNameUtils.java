package com.hngd.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class TypeNameUtils {

    private TypeNameUtils(){
        
    }

    public static String buildTypeName(Type type) {
        
        if(type==null){
            return null;
        }
        if(type instanceof ParameterizedType){
            ParameterizedType pt=(ParameterizedType) type;
            Type rawType=pt.getRawType();
            Type ownerType=pt.getOwnerType();
            Type[] subTypes=pt.getActualTypeArguments();
            String rawTypeName=buildTypeName(rawType);
            String ownerTypeName=buildTypeName(ownerType);
            StringBuilder sb=new StringBuilder();
            if(subTypes!=null){
                for(Type subType :subTypes){
                    String subTypeName=buildTypeName(subType);
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
}
