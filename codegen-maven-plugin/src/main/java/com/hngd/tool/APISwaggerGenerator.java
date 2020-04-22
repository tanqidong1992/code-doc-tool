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
import com.hngd.tool.utils.ProjectUtils;

import io.swagger.util.Json;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

@Mojo(name="api-swagger",defaultPhase = LifecyclePhase.COMPILE)
public class APISwaggerGenerator extends BaseMojo{
    private static final Logger logger=LoggerFactory.getLogger(APISwaggerGenerator.class);
	public static final String API_FILE_NAME="api.json";
	
	
	/**
	 * the directory to save swagger document json file. default value:/target/api/swagger
	 */
	@Parameter(required = false)
	public File swaggerOutput;
	
	/**
	 * controller package filter
	 */
	@Parameter(required = true)
	public String packageFilter;
	/**
	 * api config file info
	 */
	@Parameter(required = true)
	private String confFilePath;

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
		if(swaggerOutput==null){
			swaggerOutput=new File(buildOutputPath+File.separator+"api"+File.separator+"swagger");
			if(swaggerOutput.exists() || swaggerOutput.mkdirs()){
				
			}else {
				throw new MojoFailureException("创建输出目录失败:"+swaggerOutput.getAbsolutePath());
			}
		}
		String jarFilePath =ProjectUtils.buildJarFilePath(mavenProject);
		
		ServerConfig config=ServerConfig.load(confFilePath);
		File jarFile=new File(jarFilePath);
		String s=null;
		if(jarFile.exists()) {
			s=ProjectAnalysis.process(getSourceRoots(), jarFile, packageFilter, config);
		}else {
			List<File> classPaths=ProjectUtils.resolveAllClassPath(mavenProject,session,projectDependenciesResolver,projects);
			s=ProjectAnalysis.process(getSourceRoots(), classPaths, packageFilter, config);
		}
		 
		File apiJsonFile=new File(swaggerOutput, API_FILE_NAME);
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
