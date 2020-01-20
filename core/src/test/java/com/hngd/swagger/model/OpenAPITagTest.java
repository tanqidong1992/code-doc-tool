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
import com.hngd.parser.source.ParserContext;
import com.hngd.web.controller.AttachmentController;
import com.hngd.web.controller.AutoInjectParameterTestController;
import com.hngd.web.controller.RoleController;
import com.hngd.web.controller.TagController.ExistsClassTagController;
import com.hngd.web.controller.TagController.NoClassTagController;
import com.hngd.web.controller.TeacherController;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class OpenAPITagTest {

	@Test
	public  void test() throws ParseException, IOException {
		String path="../core-test/src/main/java";
		File f=new File(path);
		ParserContext pc=new ParserContext();
		pc.initRecursively(f);
		OpenAPI openapi = new OpenAPI();
		OpenAPITool tool=new OpenAPITool(openapi,pc);
 
		tool.parse(Arrays.asList(NoClassTagController.class,ExistsClassTagController.class
				));
		String data=Json.pretty(openapi);
		
		FileUtils.write(new File("./test-output/role.json"), data, Constants.DEFAULT_CHARSET);
	}

	 
}
