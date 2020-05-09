package com.hngd.tool;

import java.io.File;
import java.io.IOException;
import java.util.List;


import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.hngd.openapi.ProjectAnalysis;
import com.hngd.openapi.config.ServerConfig;
import com.hngd.openapi.validator.OpenAPIValidator;
import com.hngd.openapi.validator.ValidationResponse;
import com.hngd.s2m.OpenAPIToMarkdown;
import com.hngd.tool.utils.ProjectUtils;

import io.swagger.util.Json;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

@Mojo(name="openapi",defaultPhase = LifecyclePhase.COMPILE)
public class OpenAPIGenerator extends BaseMojo{
    private static final Logger logger=LoggerFactory.getLogger(OpenAPIGenerator.class);
	public static final String API_FILE_NAME="api.json";
	/**
	 * the directory to save openapi document json file. default value:/target/openapi
	 */
	@Parameter(required = false)
	public File openAPIOutput;
	
	/**
	 * controller package filter
	 */
	@Parameter(required = true)
	public String packageFilter;
	
	/**
	 * 不需要解析的源码文件ANT匹配规则,多个以','分割
	 */
	@Parameter(required = false)
	public String excludes;
	/**
	 * 需要解析的源码文件ANT匹配规则,多个以','分割 
	 */
	@Parameter(required = false)
	public String includes;
	/**
	 * api config file info
	 */
	@Parameter(required = true)
	private String confFilePath;

	/**
	 *转化为Markdown的模块过滤器
	 */
	@Parameter(required=false)
	private List<String> includeTags;
	/**
	 * SwaggerUI服务地址,HOST+IP,配置后将自动推送到该服务
	 */
	@Parameter(required = false)
	private String swaggerUIServer;
	
	/**
	 * 是否禁用OpenAPI文件校验,默认禁用,如果开启校验,校验失败后将不会上传到SwaggerUI服务
	 */
	@Parameter(required=false)
	private Boolean disableValidation=true;
	
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		String buildOutputPath = mavenProject.getBuild().getDirectory();
		if(openAPIOutput==null){
			openAPIOutput=new File(buildOutputPath+File.separator+"openapi");
			if(openAPIOutput.exists() || openAPIOutput.mkdirs()){
				
			}else {
				throw new MojoFailureException("创建输出目录失败:"+openAPIOutput.getAbsolutePath());
			}
		}
		String jarFilePath =ProjectUtils.buildJarFilePath(mavenProject);
		
		ServerConfig config=ServerConfig.load(confFilePath);
		File jarFile=new File(jarFilePath);
		String s=null;
		List<File> classPaths=ProjectUtils.resolveAllClassPath(mavenProject,session,projectDependenciesResolver,projects);
		List<File> sourceJarFiles=ProjectUtils.resolveSourceJarFiles(classPaths);
		if(jarFile.exists()) {
			s=ProjectAnalysis.process(getSourceRoots(),sourceJarFiles,includes,excludes, jarFile, packageFilter, config);
		}else {
			s=ProjectAnalysis.process(getSourceRoots(),sourceJarFiles,includes,excludes, classPaths, packageFilter, config);
		}
		 
		File apiJsonFile=new File(openAPIOutput, API_FILE_NAME);
		try {
			FileUtils.write(apiJsonFile, s,"utf-8");
		} catch (IOException e) {
		    getLog().error(e);
		    throw new MojoFailureException("写入OpenAPI Json文件失败",e);
		}
		if(!disableValidation) {
			ValidationResponse vr=validate(apiJsonFile);
			if(vr==null || !CollectionUtils.isEmpty(vr.getSchemaValidationMessages())) {
				if(vr!=null) {
					getLog().error(Json.pretty(vr));
				}
				throw new MojoFailureException("校验OpenAPI失败");
			}
		}
		OpenAPIToMarkdown.openAPIToMarkdown(apiJsonFile, includeTags, openAPIOutput);
		if(StringUtils.isNotEmpty(swaggerUIServer)){
			pushToSwaggerUIServer(apiJsonFile,swaggerUIServer);
		}
	}
	


	private ValidationResponse validate(File apiJsonFile) {
		ValidationResponse vr=null;
		try {
			vr=new OpenAPIValidator().validate(apiJsonFile);
		} catch (IOException e) {
			getLog().error(e);
		}
		return vr;
	}
	
	public static  void pushToSwaggerUIServer(File apiFile,String server){

		OkHttpClient client=new OkHttpClient.Builder().build();
		String url="http://"+server+"/doc/api/document/upload";
		String filename=apiFile.getName();
		MultipartBody.Part part=MultipartBody.Part.
				createFormData("file",filename,RequestBody.create(MediaType.get("application/json"),apiFile));

		MultipartBody body=new MultipartBody.Builder()
				.addPart(part)
				.build();

		Request request=new Request.Builder()
				.url(url)
				.post(body)
				.build();
		Call call=client.newCall(request);

		try(Response resp=call.execute()){
			String s=resp.body().string();
		}catch (IOException e){
			logger.error("",e);
		}

	}


	
	
	
	
	

}
