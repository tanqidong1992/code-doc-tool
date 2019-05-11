package com.hngd.swagger.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ParseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hngd.common.web.result.RestResponse;
import com.hngd.doc.core.FieldInfo;
import com.hngd.doc.core.gen.OpenAPITool;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.doc.core.parse.EntityClassCommentParser;
 

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

public class OpenAPITest {

	@Test
	public  void test() throws ParseException, IOException {
		String path="F:\\HNOE_TQD_JAVA\\JavaCode\\HNVMNS6000\\web\\src\\main\\java\\com\\hngd\\web\\controller\\CameraController.java";
		//path="F:\\HNOE_TQD_JAVA\\JavaCode\\HNWebDoc\\apidoc-core\\src\\test\\java\\com\\hngd\\swagger\\model\\CameraController.java";
		File f=new File(path);
		ControllerClassCommentParser.parse(f);
		Type type=new TypeToken<RestResponse<Camera>>() {}.getType();
		OpenAPI openapi = new OpenAPI();
		Class<?> cls = CameraController.class;
		OpenAPITool tool=new OpenAPITool(openapi);
		
		tool.parse(Arrays.asList(cls));
		
		 ObjectMapper mapper = new ObjectMapper();
	        mapper.setSerializationInclusion(Include.NON_EMPTY);
	        PrettyPrinter p=new DefaultPrettyPrinter();
	       
			mapper.setDefaultPrettyPrinter(p);
	     String s= mapper.writeValueAsString(openapi);
 
		System.out.println(s);
	}

	 
}
