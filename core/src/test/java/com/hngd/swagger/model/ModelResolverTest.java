package com.hngd.swagger.model;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;
import com.hngd.common.web.result.RestResponse;
import com.hngd.openapi.OpenAPITool;
import com.hngd.parser.entity.FieldInfo;
import com.hngd.parser.source.ParserContext;
import com.hngd.test.dto.ComplexDTO;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

public class ModelResolverTest {

	public static void main(String[] args) {
		
		Type type=new TypeToken<List<Camera>>() {}.getType();
		OpenAPI openapi = new OpenAPI();
		Class<?> clz = ComplexDTO.class;
		ParserContext pc=new ParserContext();
		pc.initSource(new File("W:\\workspaces\\build-tools\\hn-code-tool\\core-test"));
		OpenAPITool opt=new OpenAPITool(openapi, pc);
		opt.resolveType(clz, openapi);
		Json.prettyPrint(openapi);
	}
	
	@Test
	public void testModelResolve() {
		Type type=new TypeToken<RestResponse<Camera>>() {}.getType();
		OpenAPI openapi = new OpenAPI();
		Class<?> clz = Camera.class;
		OpenAPITool opt=new OpenAPITool(openapi, new ParserContext());
		opt.resolveType(type, openapi);
		Json.prettyPrint(openapi);
	}

	 
}
