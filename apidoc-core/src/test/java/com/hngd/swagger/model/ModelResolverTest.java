package com.hngd.swagger.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.hngd.common.web.result.RestResponse;
import com.hngd.doc.core.FieldInfo;
import com.hngd.doc.core.gen.OpenAPITool;
import com.hngd.doc.core.parse.EntityClassCommentParser;
 

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

public class ModelResolverTest {

	public static void main(String[] args) {
		
		Type type=new TypeToken<RestResponse<Camera>>() {}.getType();
		OpenAPI openapi = new OpenAPI();
		Class<?> clz = Camera.class;
		OpenAPITool.resolveType(type, openapi);
		System.out.println(openapi);
	}

	 
}
