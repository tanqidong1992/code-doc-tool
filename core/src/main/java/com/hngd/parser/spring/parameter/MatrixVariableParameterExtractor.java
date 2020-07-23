package com.hngd.parser.spring.parameter;

import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.web.bind.annotation.MatrixVariable;

import com.hngd.constant.HttpParameterLocation;
import com.hngd.exception.ClassParseException;
import com.hngd.openapi.entity.HttpParameter;

public class MatrixVariableParameterExtractor extends HttpParameterExtractor<MatrixVariable> {

	public MatrixVariableParameterExtractor() {
		super(HttpParameterLocation.path,MatrixVariable.class);
	}

	@Override
	public List<HttpParameter> process(Parameter parameter) {
		/**
		List<HttpParameter> httpParams=super.process(parameter);
		httpParams.forEach(hp->{
			hp.required=parameterAnnotation.required();
		});
		return httpParams;
		*/
		//TODO MatrixVariable is not supported
		ClassParseException.throwParameterParseException(parameter, "Unspported Matrix Variable", null);
	    return null;
	}
}
