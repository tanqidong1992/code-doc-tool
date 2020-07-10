package com.hngd.codegen;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hngd.classloader.ProjectClassLoader;
import com.hngd.openapi.entity.ModuleInfo;
import com.hngd.parser.source.SourceParserContext;
import com.hngd.parser.spring.ClassParser;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import io.swagger.v3.core.util.Json;


public class JavaCodeGeneratorTest1 {
	private static final Logger logger=LoggerFactory.getLogger(JavaCodeGeneratorTest1.class);
	public static void main(String[] args) throws IOException {
		
		String projectBaseDir="../../hnvmns-java-sample";
 
		List<File> sourceRoots=Arrays.asList(
				new File(projectBaseDir+"\\target\\generated-sources\\mybatis-generator"),
				new File(projectBaseDir+"\\src\\main\\java")
	    );
		String packageFilter="com.hngd.web.controller";
		//sourceRoots=Arrays.asList(new File("/work/company/projects/education-training-system/education-training-system/src/main/java"));
		String jarFilePath=
				projectBaseDir+"\\target\\hnvmns-java-sample-0.0.1.jar";
		List<File> classPaths=Arrays.asList(
				//new File(projectBaseDir,"target\\classes")
				new File(jarFilePath)
				);
		SourceParserContext pc=new SourceParserContext();
		for(File sourceRoot:sourceRoots) {
        	pc.initSource(sourceRoot);
        }		
		ProjectClassLoader loader=new ProjectClassLoader(JavaCodeGeneratorTest1.class.getClassLoader());
		for(File classFilePath:classPaths) {
		    loader.addClasspath(classFilePath.getAbsolutePath());
		}
		String outPackageName = "com.hngd.web.api";
		List<String> allClass=loader.listAllClass();
		List<Class<?>> clazzes=allClass.stream()
			.filter(name->name.startsWith(packageFilter))
	        .map(name->loadClassFromNestedJar(loader,name))
	        .filter(clazz -> clazz != null)
	        .filter(clazz->!clazz.isInterface())
			.collect(Collectors.toList());
		clazzes.stream()
		.filter(clazz -> clazz != null)
		.filter(clazz ->isModuleClass(clazz) )
		.forEach(cls -> {
			ModuleInfo moduleInfo = new ClassParser(pc.getCommentStore()).parseModule(cls).get();
			TypeSpec typeSpec = JavaAPICodeGenerator.toJavaFile("async",moduleInfo);
			String s=typeSpec.toString();
			System.out.println(s);
			JavaFile javaFile = JavaFile.builder(outPackageName, typeSpec).build();
			//javaFile.toJavaFileObject().to
		});
 

	}
	
	public static boolean isModuleClass(Class<?> clazz) {
		return clazz.getAnnotation(RequestMapping.class) != null;
	}
	
	public static Class<?> loadClassFromNestedJar(ProjectClassLoader loader,String className){
	    try {
			return loader.loadClass(className,true);
		} catch (ClassNotFoundException e) {
		    logger.error("",e);
		}
     	return null;
	}

}
