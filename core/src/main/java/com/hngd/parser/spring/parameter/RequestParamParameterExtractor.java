package com.hngd.parser.spring.parameter;

import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import com.hngd.constant.HttpParameterLocation;
import com.hngd.openapi.entity.HttpParameter;


public class RequestParamParameterExtractor extends HttpParameterExtractor<RequestParam> {

	public RequestParamParameterExtractor() {
		super(HttpParameterLocation.query,RequestParam.class);
	}

	@Override
	public List<HttpParameter> process(Parameter parameter) {
		boolean isParameterRequired=ValueConstants.DEFAULT_NONE.equals(parameterAnnotation.defaultValue())
				&& parameterAnnotation.required();
		List<HttpParameter> httpParams=super.process(parameter);
		httpParams.forEach(hp->{
			hp.required=isParameterRequired;
			hp.defaultValue=parameterAnnotation.defaultValue();
		});
		return httpParams;
		 
	}
}
