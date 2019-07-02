package com.apidoc.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import com.api.doc.ProjectAnalysis;
import com.api.doc.constant.Constant;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hngd.doc.core.gen.OpenAPITool;
import com.hngd.doc.core.parse.CommonClassCommentParser;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.doc.core.parse.EntityClassCommentParser;

import io.squark.nestedjarclassloader.NestedJarClassLoader;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

public class TestInitAllComment {
	static {
		PropertyConfigurator.configure("./log4j.properties");
	}
	static Logger logger=LoggerFactory.getLogger(TestInitAllComment.class);
	public static void main(String[] args) throws IOException {
		String src="D:\\company\\projects\\inspection-system\\education-training-system\\src\\main\\java";
		src="D:\\company\\projects\\inspection-system\\inspection-system\\src\\main\\java";
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
		
		NestedJarClassLoader loader=new NestedJarClassLoader(TestInitAllComment.class.getClassLoader(),logger);
		String jarFilePath="D:\\company\\projects\\inspection-system\\education-training-system\\target\\education-training-system-0.0.1-SNAPSHOT.jar";
		jarFilePath="D:\\company\\projects\\inspection-system\\inspection-system\\target\\inspection-system-0.0.1-SNAPSHOT.jar";
		File file=new File(jarFilePath);
		loader.addURLs(file.toURI().toURL());
		
		Info info = App.createInfo();
		OpenAPI openApi = new OpenAPI();
		openApi.setInfo(info);

		Server serversItem = new Server();
		serversItem.setUrl("https://192.168.0.144:8080/inspection");
		openApi.addServersItem(serversItem);
		OpenAPITool openAPITool = new OpenAPITool(openApi);
		
		
		List<String> allClass=loader.listAllClass("default");
		String packageFilter="com.hngd.web.controller";
		List<Class<?>> clazzes=allClass.stream()
		.filter(name->name.startsWith(packageFilter))
         .map(name->ProjectAnalysis.loadClassFromNestedJar(loader, name))
         .filter(clazz -> clazz != null)
		 .collect(Collectors.toList());
		openAPITool.parse(clazzes);
		//openAPITool.parse("com.hngd.web.controller");
		String s = toJson("",openApi);
        FileUtils.write(new File("./api.json"), s,Constant.DEFAULT_CHARSET_NAME);
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
