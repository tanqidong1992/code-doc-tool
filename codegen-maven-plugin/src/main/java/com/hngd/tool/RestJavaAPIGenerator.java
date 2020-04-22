package com.hngd.tool;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.hngd.codegen.JavaAPICodeGenerator;
import com.hngd.tool.utils.ProjectUtils;


/**
 * Hello world!
 *
 */

@Mojo(name="api-java",defaultPhase=LifecyclePhase.COMPILE)
public class RestJavaAPIGenerator extends AbstractMojo
{
	@Component
	public MavenProject mavenProject;
	/**
	 * the directory to save the package file. default value:/src/test/java
	 */
	@Parameter(required = false)
	public File javaAPIOutputDirectory;
	/**
	 * controller package filter
	 */
	@Parameter(required = true)
	public String packageFilter;
	/**
	 * output java package 
	 */
	@Parameter(required = true)
	public String apiPackage;
	
	/**
	 * system module base url,default value is "/"
	 */
	@Parameter(required = true,defaultValue="/")
	public String serviceUrl;
	
	/**
	 * 调用方式,取值async,sync,默认为sync
	 */
	@Parameter(required = false,defaultValue = "sync")
	public String invokeType;
	
	Log log;
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		log=getLog();
		String baseDir = mavenProject.getBasedir().getAbsolutePath();
		if(javaAPIOutputDirectory==null){
			javaAPIOutputDirectory=new File(baseDir+File.separator+"src"+File.separator+"test"+File.separator+"java");
			if(javaAPIOutputDirectory.exists() || javaAPIOutputDirectory.mkdirs()){
				
			}
		}
		String jarFilePath =ProjectUtils.buildJarFilePath(mavenProject);
		try {
			generateJavaAPIFile(packageFilter,invokeType,serviceUrl,apiPackage,javaAPIOutputDirectory,jarFilePath);
		} catch (IOException e) {
			throw new MojoExecutionException("", e);
		}
	}
    public static void generateJavaAPIFile(String packageFilter,String invokeType,String baseUrl,String apiPackage,File output,String jarFilePath) throws MalformedURLException, IOException {
	    List<Class<?>> clazzes=ProjectUtils.loadSpringBootJar(jarFilePath, packageFilter);  
	    clazzes.forEach(cls->{
		    JavaAPICodeGenerator.generateJavaAPIFile(cls,invokeType,baseUrl,apiPackage,output.getAbsolutePath());
		});
	}

}
