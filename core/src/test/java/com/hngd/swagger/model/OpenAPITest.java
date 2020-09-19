package com.hngd.swagger.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ParseException;
import com.google.gson.reflect.TypeToken;
import com.hngd.common.web.result.RestResponse;
import com.hngd.constant.Constants;
import com.hngd.openapi.OpenAPITool;
import com.hngd.parser.source.SourceParserContext;
import com.hngd.web.controller.AttachmentController;
import com.hngd.web.controller.AutoInjectParameterTestController;
import com.hngd.web.controller.RoleController;
import com.hngd.web.controller.TempController;
import com.hngd.web.controller.TeacherController;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class OpenAPITest {

	@Test
	public  void test() throws ParseException, IOException {
		
		String path="../core-test/src/main/java";
		File f=new File(path);
		SourceParserContext pc=new SourceParserContext();
		pc.initSource(f);
		OpenAPI openapi = new OpenAPI();
		OpenAPITool tool=new OpenAPITool(openapi,pc.getCommentStore());
		/**
		tool.parse(Arrays.asList(RoleController.class,
				AttachmentController.class,
				AutoInjectParameterTestController.class));
		*/
		tool.parse(Arrays.asList(TempController.class
				));
		String data=Json.pretty(openapi);
		
		FileUtils.write(new File("./test-output/role.json"), data, Constants.DEFAULT_CHARSET);
	}

	 
}
