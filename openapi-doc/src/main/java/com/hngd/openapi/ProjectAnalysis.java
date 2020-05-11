package com.hngd.openapi;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.openapi.config.ServerConfig;
import com.hngd.parser.source.SourceParserContext;

import io.squark.nestedjarclassloader.NestedJarClassLoader;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

public class ProjectAnalysis {

	private static final Logger logger=LoggerFactory.getLogger(ProjectAnalysis.class);
	public static String process(List<File> sourceRoots,List<File> sourceJarFiles,String includes,String excludes,File jarFilePath,String packageFilter,ServerConfig config) {
	    NestedJarClassLoader loader=new NestedJarClassLoader(ProjectAnalysis.class.getClassLoader(),logger);
		try {
			loader.addURLs(jarFilePath.toURI().toURL());
		} catch (IOException e) {
			logger.error("",e);
		}
		return doProcess(sourceRoots,sourceJarFiles,includes,excludes, loader, packageFilter, config);
	}
	
	private static String doProcess(List<File> sourceRoots,List<File> sourceJarFiles,String includes,String excludes,NestedJarClassLoader loader,String packageFilter,ServerConfig config) {
		SourceParserContext pc=new SourceParserContext(includes,excludes);
		for(File sourceRoot:sourceRoots) {
        	pc.initSource(sourceRoot);
        }
		pc.initSourceInJar(sourceJarFiles);
		OpenAPI openApi = new OpenAPI();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		config.info.setDescription("最后更新时间:"+sdf.format(new Date()));
		openApi.setInfo(config.info);

        if(config.servers!=null) {
            config.servers.forEach(s->openApi.addServersItem(s));
        }
		OpenAPITool openAPITool = new OpenAPITool(openApi,pc.getCommentStore());
		List<String> allClass=loader.listAllClass("default");
		List<Class<?>> clazzes=allClass.stream()
			.filter(name->name.startsWith(packageFilter))
	        .map(name->loadClassFromNestedJar(loader,name))
	        .filter(clazz -> clazz != null)
	        .filter(clazz->!clazz.isInterface())
			.collect(Collectors.toList());
		openAPITool.parse(clazzes);
		String s = Json.pretty(openApi);
        return s;
	}
	
	public static String process(List<File> sourceRoots,List<File> sourceJarFiles,String includes,String excludes,List<File> classFilePaths,String packageFilter,ServerConfig config) {
	    NestedJarClassLoader loader=new NestedJarClassLoader(ProjectAnalysis.class.getClassLoader(),logger);
		for(File classFilePath:classFilePaths) {
			if(classFilePath.isDirectory() || classFilePath.getName().endsWith("jar")) {
				try {
					loader.addURLs(classFilePath.toURI().toURL());
				} catch (IOException e) {
					logger.error("",e);
				}
			}else {
				//当maven依赖中存在<type>pom</type>时,pom文件会被传进来
				logger.warn("文件{},既不是目录也不是Jar",classFilePath.getAbsolutePath());
			}
			
		}
	    return doProcess(sourceRoots,sourceJarFiles,includes,excludes, loader, packageFilter, config);
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
