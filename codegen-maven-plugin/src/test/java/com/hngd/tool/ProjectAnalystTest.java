package com.hngd.tool;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.hngd.openapi.validator.OpenAPIValidator;
import com.hngd.openapi.validator.ValidationResponse;
import com.hngd.tool.config.ServerConfig;

import io.swagger.util.Json;

public class ProjectAnalystTest {

    @Test
    public void test() throws IOException {
        String projectBaseDir="../../hnvmns-java-sample";
        String confFilePath=
                projectBaseDir+"/build-config/openapi.json";
        ServerConfig config=ServerConfig.load(confFilePath).get();
        String jarFilePath=
                projectBaseDir+"/target/hnvmns-java-sample-0.0.1.jar";
        
        
        File jarFile=new File(jarFilePath);
        List<File> sourceRoots=Arrays.asList(
                //new File(projectBaseDir+"/target/generated-sources/mybatis-generator"),
                new File(projectBaseDir+"/src/main/java")
        );
        String packageFilter="com.hngd.web.controller";
        //sourceRoots=Arrays.asList(new File("/work/company/projects/education-training-system/education-training-system/src/main/java"));
        List<File> classPaths=Arrays.asList(
                new File(projectBaseDir,"target/classes")
                );
        String s=ProjectAnalyst.builder()
                .withSourceRoots(sourceRoots)
                .withClassPaths(classPaths)
                .withPackageFilter(packageFilter)
                .withServerConfig(config)
                .build()
                .process();
        
        String swaggerOutput="./test-out";
        File apiJsonFile=new File(swaggerOutput, "api.json");
        try {
            FileUtils.write(apiJsonFile, s,"utf-8");
        } catch (IOException e) {
           e.getSuppressed();
        }
        
        OpenAPIValidator validator=new OpenAPIValidator();
        ValidationResponse resp=validator.validate(s);
        Json.prettyPrint(resp);
    }
}
