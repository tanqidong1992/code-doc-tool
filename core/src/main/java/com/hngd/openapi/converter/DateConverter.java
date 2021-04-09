package com.hngd.openapi.converter;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.hngd.openapi.entity.HttpParameter;

import io.swagger.v3.oas.models.media.DateSchema;

public class DateConverter implements ParameterTypeConverter{

    @Override
    public boolean support(Type type) {
        if(! (type instanceof Class<?>)) {
            return false;
        }
        Class<?> argumentClass=(Class<?>)type;
        return Date.class.isAssignableFrom(argumentClass) 
                || LocalDate.class.isAssignableFrom(argumentClass)
                || LocalDateTime.class.isAssignableFrom(argumentClass);
    }

    @Override
    public void convert(Type type, HttpParameter pc) {
        pc.isPrimitive = true;
        DateSchema ds = new DateSchema();
        //TODO fix date format
        ds.setFormat(pc.dateFormat);
        pc.schema = ds;
    }

}
