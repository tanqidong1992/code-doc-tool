package com.hngd.swagger.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.github.javaparser.ParseException;
import com.hngd.constant.Constants;
import com.hngd.openapi.OpenAPITool;
import com.hngd.parser.source.SourceParserContext;
import com.hngd.web.controller.TagController.ExistsClassTagController;
import com.hngd.web.controller.TagController.NoClassTagController;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class OpenAPITagTest {

    @Test
    public  void test() throws ParseException, IOException {
        String path="../core-test/src/main/java";
        File f=new File(path);
        SourceParserContext pc=new SourceParserContext();
        pc.initSource(f);
        OpenAPI openapi = new OpenAPI();
        OpenAPITool tool=new OpenAPITool(openapi,pc.getCommentStore());
 
        tool.parse(Arrays.asList(NoClassTagController.class,ExistsClassTagController.class
                ));
        String data=Json.pretty(openapi);
        
        FileUtils.write(new File("./test-output/role.json"), data, Constants.DEFAULT_CHARSET);
    }

     
}
