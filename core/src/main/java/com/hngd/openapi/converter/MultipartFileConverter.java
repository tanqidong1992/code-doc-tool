package com.hngd.openapi.converter;

import java.lang.reflect.Type;

import org.springframework.web.multipart.MultipartFile;

import com.hngd.openapi.entity.HttpParameter;

import io.swagger.v3.oas.models.media.FileSchema;

public class MultipartFileConverter implements ParameterTypeConverter{

    @Override
    public boolean support(Type type) {
        if(! (type instanceof Class<?>)) {
            return false;
        }
        Class<?> argumentClass=(Class<?>)type;
        return MultipartFile.class.isAssignableFrom(argumentClass);
    }

    @Override
    public void convert(Type type, HttpParameter pc) {
        pc.isPrimitive = true;
        pc.schema = new FileSchema();
    }

}
