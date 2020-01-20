package com.hngd.parser.spring.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.hngd.constant.HttpParameterIn;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.parser.spring.MethodArgUtils;


public class RequestBodyProcessor extends HttpParameterProcessor<RequestBody> {

	public RequestBodyProcessor() {
		super(HttpParameterIn.body,RequestBody.class);
	}

	@Override
	public List<HttpParameter> process(Parameter parameter) {
		HttpParameter httpParam=new HttpParameter();
	    httpParam.name = parameter.getName();
	    httpParam.httpParamIn = HttpParameterIn.body;
	    httpParam.required = parameterAnnotation.required();
	    httpParam.javaType=parameter.getParameterizedType();
	    Annotation[] annotations=parameter.getAnnotations();
	    Optional<String> dateFormat=MethodArgUtils.extractDateFormat(annotations);
	    if(dateFormat.isPresent()) {
		    httpParam.openapiFormat=dateFormat.get();
	    }
	    httpParam.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
	    return Arrays.asList(httpParam);
		 
	}
}
