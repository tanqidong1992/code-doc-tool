package com.hngd.codegen;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hngd.codegen.JavaAPICodeGenerator;
import com.hngd.openapi.entity.ModuleInfo;
import com.hngd.parser.source.SourceParserContext;
import com.hngd.parser.spring.ClassParser;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
@Deprecated 
public class JavaCodeGeneratorTest {

	private static final Logger logger=LoggerFactory.getLogger(JavaCodeGeneratorTest.class);
	public static void main(String[] args) throws URISyntaxException, ClassNotFoundException {
		
			File out = new File(".output/java");
			if(out.exists() || out.mkdir()) {
				
			}
			String outPackageName = "com.hngd.web.api";
			String packageName = "com.hngd.web.controller";
			URL url = JavaAPICodeGenerator.class.getResource("/com/hngd/web/controller");
			Path path = Paths.get(url.toURI());
			File[] files = path.toFile().listFiles();
			Arrays.asList(files).stream().map(file -> file.getName()).filter(name -> name.endsWith(".class"))
					.map(name -> name.replace(".class", "")).map(name -> packageName + "." + name).map(name -> {
						Class<?> clazz = null;
						try {
							clazz = Class.forName(name);
						} catch (Exception e) {
							logger.error("",e);
						}
						return clazz;
					}).filter(clazz -> clazz != null).filter(clazz -> clazz.getAnnotation(RequestMapping.class) != null)
					.forEach(cls -> {
						ModuleInfo moduleInfo = new ClassParser(new SourceParserContext().getCommentStore()).parseModule(cls).get();
						TypeSpec typeSpec = JavaAPICodeGenerator.toJavaFile(null,moduleInfo);
						JavaFile javaFile = JavaFile.builder(outPackageName, typeSpec).build();
						try {
							javaFile.writeTo(out);
						} catch (Exception e) {
							logger.error("",e);
						}
					});
		}
	
}
