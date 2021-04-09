package com.hngd.openapi.converter;

import java.lang.reflect.Type;

import com.hngd.openapi.entity.HttpParameter;

public interface ParameterTypeConverter {

    boolean support(Type type);
    void convert(Type type,HttpParameter pc);
    
}
