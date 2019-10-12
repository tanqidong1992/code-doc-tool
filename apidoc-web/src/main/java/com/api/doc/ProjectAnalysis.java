package com.api.doc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.doc.config.ServerConfig;
import com.api.doc.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hngd.doc.core.gen.OpenAPITool;
import com.hngd.doc.core.parse.CommonClassCommentParser;

import io.squark.nestedjarclassloader.NestedJarClassLoader;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

public class ProjectAnalysis {

	private static final Logger logger=LoggerFactory.getLogger(ProjectAnalysis.class);
	public static String process(List<File> sourceRoots,File jarFilePath,String packageFilter,ServerConfig config) {
        for(File sourceRoot:sourceRoots) {
        	CommonClassCommentParser.initRecursively(sourceRoot);
        }
	    NestedJarClassLoader loader=new NestedJarClassLoader(ProjectAnalysis.class.getClassLoader(),logger);
		try {
			loader.addURLs(jarFilePath.toURI().toURL());
		} catch (IOException e) {
			logger.error("",e);
		}
		OpenAPI openApi = new OpenAPI();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		config.info.setDescription("最后更新时间:"+sdf.format(new Date()));
		openApi.setInfo(config.info);

        if(config.servers!=null) {
            config.servers.forEach(s->openApi.addServersItem(s));
        }
		OpenAPITool openAPITool = new OpenAPITool(openApi);
		List<String> allClass=loader.listAllClass("default");
		List<Class<?>> clazzes=allClass.stream()
			.filter(name->name.startsWith(packageFilter))
	        .map(name->loadClassFromNestedJar(loader,name))
	        .filter(clazz -> clazz != null)
	        .filter(clazz->!clazz.isInterface())
			.collect(Collectors.toList());
		openAPITool.parse(clazzes);
		String s=null;
		try {
			s = JsonUtils.toJson(openApi);
		} catch (JsonProcessingException e) {
			logger.error("",e);
		}
        return s;
	}
	
	public static Class<?> loadClassFromNestedJar(NestedJarClassLoader loader,String className){
	    try {
			return loader.loadClass(className,true);
		} catch (ClassNotFoundException e) {
		    logger.error("",e);
		}
     	return null;
	}
}
