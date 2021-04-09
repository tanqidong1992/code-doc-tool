package com.hngd.openapi.converter;

import java.lang.reflect.Type;

import com.hngd.constant.Constants;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.utils.TypeUtils;

import io.swagger.v3.oas.models.media.ObjectSchema;

public class ObjectConverter implements ParameterTypeConverter{

    @Override
    public boolean support(Type type) {
        return false;
    }

    @Override
    public void convert(Type type, HttpParameter pc) {
        pc.isPrimitive = false;
        pc.schema = new ObjectSchema();
        pc.ref = TypeUtils.toRef(type);
        pc.schema.set$ref(Constants.SCHEMA_REF_PREFIX + pc.ref);
    }

}
