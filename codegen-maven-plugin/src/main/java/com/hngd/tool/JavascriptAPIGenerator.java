package com.hngd.tool;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.hngd.codegen.AjaxCodeGenerator;
import com.hngd.codegen.AxiosCodeGenerator;
import com.hngd.codegen.exception.CodeGenerateException;
import com.hngd.openapi.entity.ModuleInfo;
import com.hngd.parser.source.ParserContext;
import com.hngd.parser.spring.ClassParser;
import com.hngd.tool.utils.ProjectUtils;

/**
 * Hello world!
 *
 */
@Mojo(name="api-js",defaultPhase=LifecyclePhase.COMPILE)
public class JavascriptAPIGenerator extends BaseMojo
{
	/**
	 * controller package filter
	 */
	@Parameter(required = true)
	public String packageFilter;
	/**
	 * the directory to save the package file. default value:/target/ajax-api
	 */
	@Parameter(required = false)
	public File outputDirectory;
	/**
	 * system module base url,default value is "/"
	 */
	@Parameter(required = true,defaultValue="/")
	public String serviceUrl;
	/**
	 * code template type,should be "ajax","axios"
	 */
	@Parameter(required = true)
	public String type;
	Log log;
	@SuppressWarnings("unused")
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		log=getLog();
		String buildOutputPath = mavenProject.getBuild().getDirectory();
		if(outputDirectory==null){
			outputDirectory=new File(buildOutputPath+File.separator+"api"+File.separator+"js");
		}
		if(outputDirectory.exists() || outputDirectory.mkdirs()){
			
		}
  
		List<File> classPaths=ProjectUtils.resolveAllClassPath(mavenProject,session,projectDependenciesResolver,projects);
		List<Class<?>> clazzes=ProjectUtils.loadProjectClass(classPaths, packageFilter);
		
		ParserContext parserContext=new ParserContext();
		for(File sourceRoot:getSourceRoots()) {
			parserContext.initSource(sourceRoot);
		}
		
		ClassParser cp=new ClassParser(parserContext);
		List<ModuleInfo> modules=new LinkedList<>();
		for(Class<?> cls:clazzes) {
			Optional<ModuleInfo> module=cp.parseModule(cls);
			module.ifPresent(modules::add);
		}
		String code=null;
		if("ajax".equals(type)) {
			AjaxCodeGenerator acg=new AjaxCodeGenerator();
			code=acg.generate(modules, serviceUrl);
		}else if("axios".equals(type)){
			AxiosCodeGenerator acg=new AxiosCodeGenerator();
			code=acg.generate(modules, serviceUrl);
		}else {
			throw new CodeGenerateException("Unspported Code Type:"+type, null);
		}
		if(!StringUtils.isEmpty(code)){
			File file=new File(outputDirectory,"client.js");
			try {
				FileUtils.write(file, code,"utf-8");
			} catch (IOException e) {
				log.error(e);
				throw new CodeGenerateException("Unspported Code Type:", e);
			}
		}
		
	}
}
