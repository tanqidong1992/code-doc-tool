package com.hngd.openapi.validator;

import org.junit.Test;

import io.swagger.v3.core.util.Json;

public class OpenAPIValidatorTest {

	@Test
	public void test() {
		OpenAPIValidator oav=new OpenAPIValidator();
		String s="{}";
		ValidationResponse vr=oav.validate(s);
		System.out.println(Json.pretty(vr));
	}
}
