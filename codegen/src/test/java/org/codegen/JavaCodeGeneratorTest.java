package org.codegen;

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

import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.gen.ClassParser;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

public class JavaCodeGeneratorTest {

	private static final Logger logger=LoggerFactory.getLogger(JavaCodeGeneratorTest.class);
	public static void main(String[] args) throws URISyntaxException, ClassNotFoundException {
		
			File out = new File("..\\webapi-test\\src\\main\\java");
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
						ModuleInfo moduleInfo = ClassParser.processClass(cls);
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
