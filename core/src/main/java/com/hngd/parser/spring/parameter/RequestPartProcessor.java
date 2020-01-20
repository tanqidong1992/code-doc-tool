package com.hngd.parser.spring.parameter;

import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.hngd.constant.HttpParameterIn;
import com.hngd.openapi.entity.HttpParameter;


public class RequestPartProcessor extends HttpParameterProcessor<RequestPart> {

	public RequestPartProcessor() {
		super(HttpParameterIn.body,RequestPart.class);
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
