package com.hngd.parser.spring.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;

import com.hngd.constant.HttpParameterLocation;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.parser.spring.MethodArgUtils;
import com.hngd.utils.RestClassUtils;

public abstract class HttpParameterExtractor<T extends Annotation> {

    protected Class<T> parameterAnnotationType;
    protected T parameterAnnotation;
    protected HttpParameterLocation location;
    
    public HttpParameterExtractor(HttpParameterLocation location,Class<T> parameterAnnotationType) {
        this.location = location;
        this.parameterAnnotationType=parameterAnnotationType;
    }
    public boolean accept(Parameter parameter) {
        Annotation[] annotations = parameter.getAnnotations();
        Optional<T> t=MethodArgUtils.extractAnnotaion(annotations, parameterAnnotationType);
        if(t.isPresent()) {
            parameterAnnotation=t.get();
            return true;
        }else {
            return false;
        }
    }
    public List<HttpParameter> process(Parameter parameter){
        HttpParameter httpParam=new HttpParameter();
        httpParam.name =RestClassUtils.extractParameterName(parameterAnnotation);
        httpParam.location = location;
        httpParam.javaType=parameter.getParameterizedType();
        Annotation[] annotations=parameter.getAnnotations();
        Optional<String> dateFormat=MethodArgUtils.extractDateFormat(annotations);
        if(dateFormat.isPresent()) {
            httpParam.dateFormat=dateFormat.get();
        }
        httpParam.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
        return Arrays.asList(httpParam);
    }
}
