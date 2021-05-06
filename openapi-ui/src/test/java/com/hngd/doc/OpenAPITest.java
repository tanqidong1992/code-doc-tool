package com.hngd.doc;

import java.io.File;
import java.util.Arrays;

import com.hngd.doc.controller.DocumentController;
import com.hngd.openapi.OpenAPITool;
import com.hngd.parser.source.SourceParserContext;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class OpenAPITest {

    public static void main(String[] args) {
        String filePath="src/main/java";
        File sourceBaseDirectory=new File(filePath);
        SourceParserContext pc=new SourceParserContext("com/hngd/**/*.java", null);
        pc.initSource(sourceBaseDirectory);
        //pc.getCommentStore().print();
        OpenAPI openAPI=new OpenAPI();
        OpenAPITool tool=new OpenAPITool(openAPI, pc.getCommentStore());
        tool.parse(Arrays.asList(DocumentController.class));
        String s=Json.pretty(openAPI);
        System.out.println(s); 
    }
}
