package com.hngd.tool;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.classloader.ProjectClassLoader;
import com.hngd.openapi.OpenAPITool;
import com.hngd.parser.source.CachedSourceParserContext;
import com.hngd.parser.source.SourceParserContext;
import com.hngd.tool.config.ServerConfig;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class ProjectAnalysis {

    private static final Logger logger=LoggerFactory.getLogger(ProjectAnalysis.class);

    private static String doProcess(List<File> sourceRoots,List<File> sourceJarFiles,
            String includes,String excludes,ProjectClassLoader loader,
            String packageFilter,ServerConfig config,File cacheDirectory) {
        SourceParserContext pc;
        if(cacheDirectory!=null) {
            pc=new CachedSourceParserContext(includes,excludes,cacheDirectory.getAbsolutePath());
        }else {
            pc=new SourceParserContext(includes,excludes);
        }
        for(File sourceRoot:sourceRoots) {
            pc.initSource(sourceRoot);
        }
        pc.initSourceInJar(sourceJarFiles);
        OpenAPI openApi = new OpenAPI();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        config.info.setDescription("最后更新时间:"+sdf.format(new Date()));
        openApi.setInfo(config.info);

        if(config.servers!=null) {
            config.servers.forEach(s->openApi.addServersItem(s));
        }
        OpenAPITool openAPITool = new OpenAPITool(openApi,pc.getCommentStore());
        List<String> allClass=loader.listAllClass();
        List<Class<?>> clazzes=allClass.stream()
            .filter(name->name.startsWith(packageFilter))
            .map(name->loadClassFromNestedJar(loader,name))
            .filter(clazz -> clazz != null)
            .filter(clazz->!clazz.isInterface())
            .collect(Collectors.toList());
        openAPITool.parse(clazzes);
        return Json.pretty(openApi);
    }

    @Deprecated
    public static String process(List<File> sourceRoots,List<File> sourceJarFiles,
            String includes,String excludes,
            File jarFilePath,String packageFilter,
            ServerConfig config) {
        return process(sourceRoots,sourceJarFiles,includes,excludes, Arrays.asList(jarFilePath),packageFilter,config,null);
    }

    public static String process(List<File> sourceRoots,List<File> sourceJarFiles,
            String includes,String excludes,
            List<File> classFilePaths,String packageFilter,
            ServerConfig config,File cacheDirectory) {
        ProjectClassLoader loader=new ProjectClassLoader(ProjectAnalysis.class.getClassLoader());
        for(File classFilePath:classFilePaths) {
            if(classFilePath.isDirectory() || classFilePath.getName().endsWith("jar")) {
                loader.addClasspath(classFilePath.getAbsolutePath());
            }else {
                //当maven依赖中存在<type>pom</type>时,pom文件会被传进来
                logger.warn("文件{},既不是目录也不是Jar",classFilePath.getAbsolutePath());
            }
        }
        return doProcess(sourceRoots,sourceJarFiles,includes,excludes, loader, packageFilter, config,cacheDirectory);
    }

    public static Class<?> loadClassFromNestedJar(ProjectClassLoader loader,String className){
        try {
            return loader.loadClass(className,true);
        } catch (ClassNotFoundException e) {
            logger.error("",e);
        }
         return null;
    }
}
