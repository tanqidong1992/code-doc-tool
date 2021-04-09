package com.hngd.openapi.converter;

import java.lang.reflect.Type;
import java.util.Map;

import org.springframework.util.MultiValueMap;

import com.hngd.openapi.entity.HttpParameter;

import io.swagger.v3.oas.models.media.ObjectSchema;

public class MapOrMultiValueMapConverter implements ParameterTypeConverter{

    @Override
    public boolean support(Type type) {
        if(! (type instanceof Class<?>)) {
            return false;
        }
        Class<?> argumentClass=(Class<?>)type;
        return Map.class.isAssignableFrom(argumentClass) ||
                MultiValueMap.class.isAssignableFrom(argumentClass);
    }

    @Override
    public void convert(Type type, HttpParameter pc) {
        pc.isPrimitive = false;
        pc.schema = new ObjectSchema();
    }

}
