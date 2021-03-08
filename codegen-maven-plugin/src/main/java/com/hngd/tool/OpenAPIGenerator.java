package com.hngd.tool;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import okhttp3.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Developer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.hngd.openapi.validator.OpenAPIValidator;
import com.hngd.openapi.validator.ValidationResponse;
import com.hngd.s2m.OpenAPIToMarkdown;
import com.hngd.tool.config.ServerConfig;
import com.hngd.tool.utils.ProjectUtils;

import io.swagger.util.Json;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Mojo(name="openapi",defaultPhase = LifecyclePhase.COMPILE)
public class OpenAPIGenerator extends BaseMojo{
    private static final Logger logger=LoggerFactory.getLogger(OpenAPIGenerator.class);
    public static final String API_FILE_NAME="openapi.json";
    /**
     * 用于保存OpenAPI文档的目录. 默认值为:${basedir}/target/openapi
     */
    @Parameter(required = false)
    public File openAPIOutput;
    /**
     * Spring控制器类所在包的包名前缀
     */
    @Parameter(required = true)
    public String packageFilter;
    
    /**
     * 不需要解析的源码文件路径ANT匹配规则,多个以','分割
     */
    @Parameter(required = false)
    public String excludes;
    /**
     * 需要解析的源码文件路径ANT匹配规则,多个以','分割 
     */
    @Parameter(required = false)
    public String includes;
    /**
     * OpenAPI基础信息配置文件,默认取值为 ${basedir}/build-config/openapi.json
     */
    @Parameter(required = false)
    private String confFilePath;
    /**
     *转化为Markdown的模块过滤器
     */
    @Parameter(required=false)
    private List<String> includeTags;
    /**
     * SwaggerUI服务地址,HOST+IP,配置后将自动将生成的openapi文档推送到该服务
     */
    @Parameter(required = false)
    private String swaggerUIServer;
    
    /**
     * 是否禁用OpenAPI文件校验,默认禁用,如果开启校验,校验失败后将不会上传到SwaggerUI服务
     */
    @Parameter(required=false)
    private Boolean disableValidation=true;
    
    /**
     * openapi文档中的server url,默认为 http://localhost:8080
     */
    @Parameter(required = false,defaultValue = "http://localhost:8080")
    private String openAPIServerURL;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(openAPIOutput==null){
            String buildOutputPath = mavenProject.getBuild().getDirectory();
            openAPIOutput=new File(buildOutputPath+File.separator+"openapi");
            if(openAPIOutput.exists() || openAPIOutput.mkdirs()){
                
            }else {
                throw new MojoFailureException("创建输出目录失败:"+openAPIOutput.getAbsolutePath());
            }
        }
        
        if(confFilePath==null) {
            confFilePath=mavenProject.getBasedir().getAbsolutePath()
                    +File.separator+"build-config"+
                    File.separator+"openapi.json";
            getLog().info("Using the default openapi config file path:"+confFilePath);
        }
        //判断配置文件是否存在
        ServerConfig config;
        if(!(new File(confFilePath).exists())) {
            //throw new MojoFailureException("找不到OpenAPI基础信息配置文件:"+confFilePath);
            config=new ServerConfig();
        }else {
            Optional<ServerConfig> loadResult=ServerConfig.load(confFilePath);
            if(loadResult.isPresent()) {
                config=loadResult.get();
            }else {
                config=new ServerConfig();
            }
        }
        autoFillConfig(config);
        
        List<File> classPaths=ProjectUtils.resolveAllClassPath(mavenProject,session,projectDependenciesResolver,projects);
        getLog().debug("---class paths---");
        classPaths.forEach(cp->{
            getLog().debug(cp.getAbsolutePath());
        });
        getLog().debug("---class paths end---");
        List<File> sourceJarFiles=ProjectUtils.resolveSourceJarFiles(classPaths);
        String s=ProjectAnalysis.process(getSourceRoots(),sourceJarFiles,includes,excludes, classPaths, packageFilter, config);
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
 
    private void autoFillConfig(ServerConfig config) {
        
        if(CollectionUtils.isEmpty(config.servers)) {
            Server server=new Server();
            server.setUrl(openAPIServerURL);
            config.servers=Arrays.asList(server);
        }
        if(config.info==null) {
            config.info=new Info();
        }
        Info info=config.info;
        if(StringUtils.isEmpty(info.getTitle())) {
            String title=mavenProject.getName();
            if(StringUtils.isEmpty(title)) {
                title=mavenProject.getArtifactId();
            }
            info.setTitle(title);
        }
        if(StringUtils.isEmpty(info.getDescription())) {
            info.setDescription(mavenProject.getDescription());
        }
        if(StringUtils.isEmpty(info.getVersion())) {
            info.setVersion(mavenProject.getVersion());
        }
        Contact contact=info.getContact();
        if(contact==null) {
            List<Developer> developers=mavenProject.getDevelopers();
            if(!CollectionUtils.isEmpty(developers)) {
                Developer firstDeveloper=developers.get(0);
                contact=new Contact();
                info.setContact(contact);
                contact.setEmail(firstDeveloper.getEmail());
                contact.setName(firstDeveloper.getName());
                contact.setUrl(firstDeveloper.getUrl());
            }
        }
        License license=info.getLicense();
        if(license==null) {
            List<org.apache.maven.model.License> mls=mavenProject.getLicenses();
            if(!CollectionUtils.isEmpty(mls)) {
                org.apache.maven.model.License ml=mls.get(0);
                license=new License();
                license.setName(ml.getName());
                license.setUrl(ml.getUrl());
                info.setLicense(license);
            }
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
            logger.info("push openapi json result:{}",s);
        }catch (IOException e){
            logger.error("",e);
        }
    }
}
