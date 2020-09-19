package com.hngd.parser.spring.parameter;

import java.lang.reflect.Parameter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ValueConstants;

import com.hngd.constant.HttpParameterLocation;
import com.hngd.openapi.entity.HttpParameter;


public class CookieValueParameterExtractor extends HttpParameterExtractor<CookieValue> {

	public CookieValueParameterExtractor() {
		super(HttpParameterLocation.cookie,CookieValue.class);
	}

	@Override
	public List<HttpParameter> process(Parameter parameter) {
		List<HttpParameter> httpParams=super.process(parameter);
		boolean isParameterRequired=ValueConstants.DEFAULT_NONE.equals(parameterAnnotation.defaultValue())
				&& parameterAnnotation.required();
		httpParams.forEach(hp->{
			hp.required=isParameterRequired;
			hp.defaultValue=parameterAnnotation.defaultValue();
		});
		return httpParams;
		 
	}
}
