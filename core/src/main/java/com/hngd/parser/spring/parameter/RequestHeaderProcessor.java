package com.hngd.parser.spring.parameter;

import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.hngd.constant.HttpParameterIn;
import com.hngd.openapi.entity.HttpParameter;


public class RequestHeaderProcessor extends HttpParameterProcessor<RequestHeader> {

	public RequestHeaderProcessor() {
		super(HttpParameterIn.header,RequestHeader.class);
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
