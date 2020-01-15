package com.hngd.parser;

import java.io.File;
import java.lang.reflect.Type;

import com.hngd.test.dto.Teacher;
import com.hngd.openapi.OpenAPITool;
import com.hngd.parser.source.CommonClassCommentParser;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class InheritedClassFiledCommentParser {

	public static void main(String[] args) {
		
		String rootPath="../core-test/src/main/java";
		File directory=new File(rootPath);
		CommonClassCommentParser.initRecursively(directory);
		//CommonClassCommentParser.printResult();
		Type type=Teacher.class;
		OpenAPI swagger=new OpenAPI();
		OpenAPITool.resolveType(type, swagger);
		Json.prettyPrint(swagger);

	}

}
