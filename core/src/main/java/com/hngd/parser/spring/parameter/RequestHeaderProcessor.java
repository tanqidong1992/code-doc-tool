package com.hngd.parser.spring.parameter;

import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.web.bind.annotation.RequestHeader;

import com.hngd.constant.HttpParameterLocation;
import com.hngd.openapi.entity.HttpParameter;


public class RequestHeaderProcessor extends HttpParameterProcessor<RequestHeader> {

	public RequestHeaderProcessor() {
		super(HttpParameterLocation.header,RequestHeader.class);
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
