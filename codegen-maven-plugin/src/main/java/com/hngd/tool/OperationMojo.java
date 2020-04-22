package com.hngd.tool;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.openapi.ProjectAnalysis;
import com.hngd.operation.log.annotation.LogAnnotation;
import com.hngd.operation.log.constant.Constants;
import com.hngd.parser.entity.ClassInfo;
import com.hngd.parser.source.ParserContext;
import com.hngd.parser.spring.SpringAnnotationUtils;
import com.hngd.tool.utils.ProjectUtils;

import io.squark.nestedjarclassloader.NestedJarClassLoader;
/**
 * 
 * @author hnoe-dev-tqd
 *
 */
@Mojo(name="operation",defaultPhase = LifecyclePhase.COMPILE)
public class OperationMojo extends AbstractMojo{
	private static final Logger logger=LoggerFactory.getLogger(OperationMojo.class);
	@Component
	public MavenProject mavenProject;
	/**
	 * controller package filter
	 */
	@Parameter(required = true)
	public String packageFilter;
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String jarFilePath =ProjectUtils.buildJarFilePath(mavenProject);
		ParserContext parserContext=new ParserContext();
		List<File> sourceRoots=mavenProject.getCompileSourceRoots().stream()
		    .map(File::new)
		    .collect(Collectors.toList());
		  for(File sourceRoot:sourceRoots) {
			  parserContext.initRecursively(sourceRoot);
	        }
		  File jarFile=new File(jarFilePath);
		  NestedJarClassLoader loader=new NestedJarClassLoader(OperationMojo.class.getClassLoader(),logger);
		  try {
				loader.addURLs(jarFile.toURI().toURL());
		  } catch (IOException e) {
				logger.error("",e);
		  }
		  List<String> allClass=loader.listAllClass("default");
		  List<Class<?>> clazzes=allClass.stream()
				.filter(name->name.startsWith(packageFilter))
		        .map(name->ProjectAnalysis.loadClassFromNestedJar(loader,name))
		        .filter(clazz -> clazz != null)
		        .filter(clazz->!clazz.isInterface())
				.collect(Collectors.toList());
		  StringBuilder sb=new StringBuilder();
		  clazzes.stream()
		      .filter(SpringAnnotationUtils::isControllerClass)
		      .flatMap(clazz->{
		    	  Method[] methods=clazz.getDeclaredMethods(); 
		    	  return Arrays.asList(methods).stream();
		      })
		      .filter(m->m.getAnnotation(LogAnnotation.class)!=null)
		      .forEach(m->{
		    	  ClassInfo ci=parserContext.getClassComment(m.getDeclaringClass());
		    	  String mc=parserContext.getMethodComment(m);
		    	  LogAnnotation la=m.getAnnotation(LogAnnotation.class);
		    	  String moduleName=la.moduleName();
		    	  if(Constants.DEFAULT_MODULE_NAME.equals(moduleName)) {
		    		  moduleName=m.getDeclaringClass().getName();
		    	  }
		    	  String operationName=la.operationName();
                  if(Constants.DEFAULT_OPERATION_NAME.equals(operationName)) {
                	  operationName=m.getName();
		    	  }
		    	  String mk=moduleName+"#"+operationName;
		    	  if(StringUtils.isEmpty(ci.getComment())) {
		    		  throw new RuntimeException("LogAnnotation注解修饰的接口所在类["+m.getDeclaringClass().getName()+"]没有注释");
		    	  }
		    	  if(StringUtils.isEmpty(mc)) {
		    		  throw new RuntimeException("LogAnnotation注解修饰的接口["+m.getDeclaringClass().getName()+"."+m.getName()+"]没有注释");
		    	  }
		    	  String s=mk+"#"+ci.getComment()+"#"+mc;
		    	  sb.append(s).append("\n");
		    	  
		      });
		  String buildOutputPath = mavenProject.getBuild().getDirectory();
		  File file=new File(buildOutputPath,"operation.txt");
		  
		try {
			FileUtils.write(file, sb.toString(), Charset.forName("utf-8"));
		} catch (IOException e) {
			logger.error("",e);
		}
	}
	
	//public static boolean 

}
