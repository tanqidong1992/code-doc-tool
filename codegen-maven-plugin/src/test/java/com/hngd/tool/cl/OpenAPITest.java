package com.hngd.tool.cl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.hngd.openapi.ProjectAnalysis;
import com.hngd.openapi.config.ServerConfig;
import com.hngd.openapi.validator.OpenAPIValidator;
import com.hngd.openapi.validator.ValidationResponse;

import io.swagger.util.Json;

public class OpenAPITest {

    public static void main(String[] args) throws IOException {
        
        String projectBaseDir="W:\\workspaces\\build-tools\\hnvmns-java-sample";
        String confFilePath=
                projectBaseDir+"\\build-config\\swagger-config.json";
        ServerConfig config=ServerConfig.load(confFilePath);
        String jarFilePath=
                projectBaseDir+"\\target\\hnvmns-java-sample-0.0.1.jar";
        
        
        File jarFile=new File(jarFilePath);
        List<File> sourceRoots=Arrays.asList(
                new File(projectBaseDir+"\\target\\generated-sources\\mybatis-generator"),
                new File(projectBaseDir+"\\src\\main\\java")
        );
        String packageFilter="com.hngd.web.controller";
        //sourceRoots=Arrays.asList(new File("/work/company/projects/education-training-system/education-training-system/src/main/java"));
        List<File> classPaths=Arrays.asList(
                new File(projectBaseDir,"target\\classes")
                );
                
        String s=ProjectAnalysis.process(sourceRoots,null,null,null, jarFile, packageFilter, config);
        String swaggerOutput="./test-out";
        File apiJsonFile=new File(swaggerOutput, "api.json");
        try {
            FileUtils.write(apiJsonFile, s,"utf-8");
        } catch (IOException e) {
           e.getSuppressed();
        }
        
        OpenAPIValidator validator=new OpenAPIValidator();
        ValidationResponse resp=validator.validate(apiJsonFile);
        Json.prettyPrint(resp);

    }

}
