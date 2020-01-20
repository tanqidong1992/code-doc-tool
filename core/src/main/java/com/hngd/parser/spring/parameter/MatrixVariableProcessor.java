package com.hngd.parser.spring.parameter;

import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;

import com.hngd.constant.HttpParameterIn;
import com.hngd.exception.ClassParseException;
import com.hngd.openapi.entity.HttpParameter;

public class MatrixVariableProcessor extends HttpParameterProcessor<MatrixVariable> {

	public MatrixVariableProcessor() {
		super(HttpParameterIn.path,MatrixVariable.class);
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
