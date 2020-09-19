package com.hngd.parser;

import java.io.File;
import java.lang.reflect.Type;

import com.hngd.test.dto.Teacher;
import com.hngd.openapi.OpenAPITool;
import com.hngd.openapi.TypeResolver;
import com.hngd.parser.source.SourceParserContext;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class InheritedClassFiledCommentParser {

	public static void main(String[] args) {
		SourceParserContext pc=new SourceParserContext();
		String rootPath="../core-test/src/main/java";
		File directory=new File(rootPath);
		pc.initSource(directory);
		//CommonClassCommentParser.printResult();
		Type type=Teacher.class;
		OpenAPI openAPI=new OpenAPI();
		OpenAPITool opt=new OpenAPITool(openAPI, pc.getCommentStore());
		TypeResolver tr=new TypeResolver(pc.getCommentStore());
		tr.resolveType(type, openAPI);
		Json.prettyPrint(openAPI);

	}

}
