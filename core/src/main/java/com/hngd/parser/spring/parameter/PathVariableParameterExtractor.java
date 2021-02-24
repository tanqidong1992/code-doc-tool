package com.hngd.parser.spring.parameter;

import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import com.hngd.constant.HttpParameterLocation;
import com.hngd.openapi.entity.HttpParameter;

public class PathVariableParameterExtractor extends HttpParameterExtractor<PathVariable> {

    public PathVariableParameterExtractor() {
        super(HttpParameterLocation.path,PathVariable.class);
    }

    @Override
    public List<HttpParameter> process(Parameter parameter) {
        List<HttpParameter> httpParams=super.process(parameter);
        httpParams.forEach(hp->{
            hp.required=parameterAnnotation.required();
        });
        return httpParams;
    }
}
