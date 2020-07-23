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
import com.hngd.parser.source.SourceParserContext;
import com.hngd.parser.spring.ClassParser;
import com.hngd.tool.utils.ProjectUtils;

/**
 * 生成javascript接口代码的mojo
 * @author tqd
 *
 */
@Mojo(name="api-js",defaultPhase=LifecyclePhase.COMPILE)
public class JavascriptAPIGenerator extends BaseMojo
{
	/**
	 *Spring控制器类所在包的包名前缀
	 */
	@Parameter(required = true)
	public String packageFilter;
	/**
	 * 用于保存生成代码的目录. 默认值为:${basedir}/target/openapi/client/js
	 */
	@Parameter(required = false)
	public File outputDirectory;
	/**
	 * 系统模块的url前缀,默认为'/'
	 */
	@Parameter(required = true,defaultValue="/")
	public String serviceUrl;
	/**
	 * 生成代码样式可取值 ajax,axios;ajax表示生成基于ajax的接口,axios表示生成基于axios的接口,默认为axios
	 */
	@Parameter(required = true,defaultValue = "axios")
	public String jsClientType;
	Log log;
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		log=getLog();
		if(outputDirectory==null){
			String buildOutputPath = mavenProject.getBuild().getDirectory();
			outputDirectory=new File(buildOutputPath+
					File.separator+"openapi"+
					File.separator+"client"+
					File.separator+"js");
		}
		if(outputDirectory.exists() || outputDirectory.mkdirs()){
			
		}
  
		List<File> classPaths=ProjectUtils.resolveAllClassPath(mavenProject,session,projectDependenciesResolver,projects);
		List<Class<?>> clazzes=ProjectUtils.loadProjectClass(classPaths, packageFilter);
		
		SourceParserContext parserContext=new SourceParserContext();
		for(File sourceRoot:getSourceRoots()) {
			parserContext.initSource(sourceRoot);
		}
		
		ClassParser cp=new ClassParser(parserContext.getCommentStore());
		List<ModuleInfo> modules=new LinkedList<>();
		for(Class<?> cls:clazzes) {
			Optional<ModuleInfo> module=cp.parseModule(cls);
			module.ifPresent(modules::add);
		}
		String code=null;
		if("ajax".equals(jsClientType)) {
			AjaxCodeGenerator acg=new AjaxCodeGenerator();
			code=acg.generate(modules, serviceUrl);
		}else if("axios".equals(jsClientType)){
			AxiosCodeGenerator acg=new AxiosCodeGenerator();
			code=acg.generate(modules, serviceUrl);
		}else {
			throw new CodeGenerateException("Unspported Code Type:"+jsClientType, null);
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
