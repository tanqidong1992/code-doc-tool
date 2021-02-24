package com.hngd.base;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ParseException;
import com.google.gson.reflect.TypeToken;
import com.hngd.common.web.result.RestResponse;
import com.hngd.constant.Constants;
import com.hngd.openapi.OpenAPITool;
import com.hngd.openapi.config.ServerConfig;
import com.hngd.parser.source.SourceParserContext;
import com.hngd.web.controller.AttachmentController;
import com.hngd.web.controller.AutoInjectParameterTestController;
import com.hngd.web.controller.RoleController;
import com.hngd.web.controller.TempController;
import com.hngd.web.controller.TeacherController;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class OpenAPITest {

    @Test
    public  void test() throws ParseException, IOException {
        
        String path="../core-test/src/main/java";
        File f=new File(path);
        SourceParserContext pc=new SourceParserContext();
        pc.initSource(f);
        OpenAPI openapi = new OpenAPI();
        ServerConfig config=ServerConfig.load("test-data/openapi-info.json");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        config.info.setDescription("最后更新时间:"+sdf.format(new Date()));
        openapi.setInfo(config.info);
        
        OpenAPITool tool=new OpenAPITool(openapi,pc.getCommentStore());
        /**
        tool.parse(Arrays.asList(RoleController.class,
                AttachmentController.class,
                AutoInjectParameterTestController.class));
        */
        tool.parse(Arrays.asList(TempController.class
                ));
        String data=Json.pretty(openapi);
        
        FileUtils.write(new File("./test-output/role.json"), data, Constants.DEFAULT_CHARSET);
    }

     
}
