package com.hngd.openapi;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Assert;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hngd.base.OpenAPIUtils;
import com.hngd.parser.source.CommentStore;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.Data;

public class DateFormatTest {

    @org.junit.Test
    public void test() {
        OpenAPI openAPI=new OpenAPI();
        CommentStore commentStore=new CommentStore();
        OpenAPITool t=new OpenAPITool(openAPI,commentStore);
        t.parse(Test.class);
        Json.prettyPrint(openAPI);
        Operation op=OpenAPIUtils.getOperation(openAPI, "/test", "get").get();
        Parameter localDate=OpenAPIUtils.parameterOfOperation(op, "localDate").get();
        Assert.assertTrue(localDate.getSchema().getFormat()
                .equals("yyyy-MM-dd"));
        
        Parameter localDateTime=OpenAPIUtils.parameterOfOperation(op, "localDateTime").get();
        Assert.assertTrue(localDateTime.getSchema().getFormat()
                .equals("yyyy-MM-dd HH:mm:ss"));
        
        Parameter date=OpenAPIUtils.parameterOfOperation(op, "date").get();
        Assert.assertTrue(date.getSchema().getFormat()
                .equals("yyyy-MM-dd"));
        
        Parameter birthday=OpenAPIUtils.parameterOfOperation(op, "birthday").get();
        Assert.assertTrue(date.getSchema().getFormat()
                .equals("yyyy-MM-dd"));
         
    }
    @Controller
    public static class Test{
        
        @GetMapping("/test")
        public String echo(
                Person person,
                @RequestParam("localDate")@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate localDate,
                @RequestParam("localDateTime")@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime localDateTime,
                @RequestParam("date")@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate date
                ) {
            return null;
        }
    }
    @Data
    public static class Person{
        @DateTimeFormat(pattern="yyyy-MM-dd") 
        private LocalDate birthday;
    }
}
