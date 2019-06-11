package com.apidoc.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hngd.doc.core.gen.OpenAPITool;
import com.hngd.doc.core.parse.CommonClassCommentParser;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.doc.core.parse.EntityClassCommentParser;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

public class TestInitAllComment {
	 
	public static void main(String[] args) throws IOException {
		String src="D:\\company\\projects\\inspection-system\\inspection-system\\src\\main\\java";
		File root=new File(src);
		long startTime = System.currentTimeMillis();
		Thread t1 = new Thread(() -> {
		    CommonClassCommentParser.initRecursively(root);
		});
		t1.start();
		try {
			t1.join();
		 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//CommonClassCommentParser.printResult();
		Info info = App.createInfo();
		OpenAPI openApi = new OpenAPI();
		openApi.setInfo(info);

		Server serversItem = new Server();
		serversItem.setUrl("https://192.168.0.144:8080/inspection");
		openApi.addServersItem(serversItem);

		//Map<String, Model> definitions = new HashMap<String, Model>();

		App.resolvePacakge("com.hngd.model", openApi);
		OpenAPITool openAPITool = new OpenAPITool(openApi);
		openAPITool.parse("com.hngd.web.controller");
		String s = toJson("",openApi);
        FileUtils.write(new File("./api.json"), s);
	}

	public static String toJson(final String key, OpenAPI mOpenAPI) throws JsonProcessingException {

		// SpecFilter sf = new SpecFilter();
		// OpenAPISpecFilter ssf=new ;
		// Swagger swagger = sf.filter(mSwagger, ssf, null, null, null);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		return mapper.writeValueAsString(mOpenAPI);
	}
}
