package com.hngd.openapi;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.hngd.parser.source.CommentStore;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class DateformatTest {

    @org.junit.Test
    public void test() {
        OpenAPI openAPI=new OpenAPI();
        CommentStore commentStore=new CommentStore();
        OpenAPITool t=new OpenAPITool(openAPI,commentStore);
        t.parse(Test.class);
        Json.prettyPrint(openAPI);
        openAPI.getPaths().get("/test").getGet().getParameters().get(0).getSchema().getFormat()
            .equals("yyyy-MM-dd");
    }
    @Controller
    public static class Test{
        
        @GetMapping("/test")
        public String echo(@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate date) {
            return null;
        }
    }
}
