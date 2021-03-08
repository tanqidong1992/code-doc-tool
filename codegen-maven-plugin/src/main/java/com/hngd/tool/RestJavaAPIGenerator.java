package com.hngd.tool;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

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
 * 用于生成Java Client的Mojo
 * @author tqd
 *
 */
@Mojo(name="api-java",defaultPhase=LifecyclePhase.COMPILE)
public class RestJavaAPIGenerator extends BaseMojo
{
    @Component
    public MavenProject mavenProject;
    /**
     * 用来保存客户端接口代码的路径,默认值为:${basedir}/target/openapi/client/java
     */
    @Parameter(required = false)
    public File javaAPIOutputDirectory;
    /**
     * Spring控制器类所在包的包名前缀
     */
    @Parameter(required = true)
    public String packageFilter;
    /**
     * 输出客户端类的报名
     */
    @Parameter(required = true)
    public String apiPackage;
    
    /**
     * 系统模块的url前缀
     */
    @Parameter(required = true,defaultValue="/")
    public String serviceUrl;
    
    /**
     * 生成接口的样式,取值async,sync,async表示生成异步接口,sync表示生成同步接口,默认为sync
     */
    @Parameter(required = false,defaultValue = "sync")
    public String invokeType;
    
    Log log;
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log=getLog();
        if(javaAPIOutputDirectory==null){
            String buildOutputPath = mavenProject.getBuild().getDirectory();
            javaAPIOutputDirectory=new File(buildOutputPath+
                    File.separator+"openapi"+
                    File.separator+"client"+
                    File.separator+"java");
            if(javaAPIOutputDirectory.exists() || javaAPIOutputDirectory.mkdirs()){
                
            }
        }
        List<File> classpaths=ProjectUtils.resolveAllClassPath(mavenProject,session,projectDependenciesResolver,projects);
        try {
            generateJavaAPIFile(packageFilter,invokeType,serviceUrl,apiPackage,javaAPIOutputDirectory,classpaths);
        } catch (IOException e) {
            throw new MojoExecutionException("", e);
        }
    }
    public static void generateJavaAPIFile(String packageFilter,String invokeType,String baseUrl,String apiPackage,File output,List<File> classpaths) throws MalformedURLException, IOException {
        List<Class<?>> clazzes=ProjectUtils.loadControllerClass(classpaths, packageFilter);  
        clazzes.forEach(cls->{
            JavaAPICodeGenerator.generateJavaAPIFile(cls,invokeType,baseUrl,apiPackage,output.getAbsolutePath());
        });
    }

}
