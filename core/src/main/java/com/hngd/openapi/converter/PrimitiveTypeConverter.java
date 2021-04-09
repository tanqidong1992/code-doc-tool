package com.hngd.openapi.converter;

import java.lang.reflect.Type;

import com.hngd.openapi.entity.HttpParameter;

import io.swagger.v3.core.util.PrimitiveType;

public class PrimitiveTypeConverter implements ParameterTypeConverter{

    @Override
    public boolean support(Type type) {
        if(! (type instanceof Class<?>)) {
            return false;
        }
        Class<?> argumentClass=(Class<?>)type;
        return Number.class.isAssignableFrom(argumentClass) ||
                String.class.isAssignableFrom(argumentClass) ||
                Boolean.class.isAssignableFrom(argumentClass);
    }

    @Override
    public void convert(Type type, HttpParameter pc) {
        PrimitiveType pt=PrimitiveType.fromType((Class<?>)type);
        pc.schema = pt.createProperty();
        pc.isPrimitive = true;
    }

}
