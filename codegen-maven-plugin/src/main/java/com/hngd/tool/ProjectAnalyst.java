package com.hngd.tool;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.classloader.ProjectClassLoader;
import com.hngd.openapi.OpenAPITool;
import com.hngd.parser.source.CachedSourceParserContext;
import com.hngd.parser.source.SourceParserContext;
import com.hngd.tool.config.OpenAPIConfig;
import com.hngd.tool.utils.ProjectUtils;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class ProjectAnalyst {

    private List<File> sourceRoots;
    private List<File> sourceJarFiles;
    private List<File> classPaths;
    private String includes;
    private String excludes;
    private String packageFilter;
    private OpenAPIConfig config;
    private File cacheDirectory;
    
    @SuppressWarnings("unused")
    private static final Logger logger=LoggerFactory.getLogger(ProjectAnalyst.class);
    
    private ProjectAnalyst() {}
    
    public static ProjectAnalystBuilder builder() {
        return new ProjectAnalystBuilder();
    }
    
    public static class ProjectAnalystBuilder{
        private ProjectAnalyst projectAnalyst=new ProjectAnalyst();
        private ProjectAnalystBuilder() {}
        public ProjectAnalystBuilder withSourceRoots(List<File> sourceRoots) {
            projectAnalyst.sourceRoots=sourceRoots;
            return this;
        }
        public ProjectAnalystBuilder withSourceJarFiles(List<File> sourceJarFiles) {
            projectAnalyst.sourceJarFiles=sourceJarFiles;
            return this;
        }
        public ProjectAnalystBuilder withIncludes(String includes) {
            projectAnalyst.includes=includes;
            return this;
        }
        public ProjectAnalystBuilder withExcludes(String excludes) {
            projectAnalyst.excludes=excludes;
            return this;
        }
        public ProjectAnalystBuilder withPackageFilter(String packageFilter) {
            projectAnalyst.packageFilter=packageFilter;
            return this;
        }
        public ProjectAnalystBuilder withServerConfig(OpenAPIConfig config) {
            projectAnalyst.config=config;
            return this;
        }
        public ProjectAnalystBuilder withCacheDirectory(File cacheDirectory) {
            projectAnalyst.cacheDirectory=cacheDirectory;
            return this;
        }
        public ProjectAnalystBuilder withClassPaths(List<File> classPaths) {
            projectAnalyst.classPaths=classPaths;
            return this;
        }
        public ProjectAnalyst build() {
            return projectAnalyst;
        }
    }
    public String process() {
        ProjectClassLoader loader=new ProjectClassLoader(ProjectAnalyst.class.getClassLoader());
        classPaths.stream()
            .filter(classpath->classpath.isDirectory() || classpath.getName().endsWith("jar"))
            .forEach(classpath->loader.addClasspath(classpath.getAbsolutePath()));
        SourceParserContext sourceParserContext;
        if(cacheDirectory!=null) {
            sourceParserContext=new CachedSourceParserContext(includes,excludes,cacheDirectory.getAbsolutePath());
        }else {
            sourceParserContext=new SourceParserContext(includes,excludes);
        }
        for(File sourceRoot:sourceRoots) {
            sourceParserContext.initSource(sourceRoot);
        }
        sourceParserContext.initSourceInJar(sourceJarFiles);
        OpenAPI openApi = new OpenAPI();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        config.info.setDescription("最后更新时间:"+sdf.format(new Date()));
        openApi.setInfo(config.info);
        if(config.servers!=null) {
            config.servers.forEach(openApi::addServersItem);
        }
        OpenAPITool openAPITool = new OpenAPITool(openApi,sourceParserContext.getCommentStore());
        List<String> allClass=loader.listAllClass();
        List<Class<?>> clazzes=allClass.stream()
            .filter(name->name.startsWith(packageFilter))
            .map(name->ProjectUtils.loadClassFromNestedJar(loader,name))
            .filter(clazz -> clazz != null)
            .filter(clazz->!clazz.isInterface())
            .collect(Collectors.toList());
        openAPITool.parse(clazzes);
        return Json.pretty(openApi);
    }
}
