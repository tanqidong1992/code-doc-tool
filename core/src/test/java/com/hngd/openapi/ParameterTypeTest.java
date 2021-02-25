package com.hngd.openapi;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.base.OpenAPIUtils;


import com.hngd.parser.source.CommentStore;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;

public class ParameterTypeTest {

    @Test
    public void test() {
        OpenAPI openAPI=new OpenAPI();
        CommentStore commentStore=new CommentStore();
        OpenAPITool t=new OpenAPITool(openAPI, commentStore);
        t.parse(Arrays.asList(A.class));
        Json.prettyPrint(openAPI);
        Operation op=OpenAPIUtils.getOperation(openAPI, "/a", "get").get();
       
        Parameter name=OpenAPIUtils.parameterOfOperation(op, "name").get();
        Parameter i32=OpenAPIUtils.parameterOfOperation(op, "i32").get();
        Parameter i64=OpenAPIUtils.parameterOfOperation(op, "i64").get();
        Parameter f32=OpenAPIUtils.parameterOfOperation(op, "f32").get();
        Parameter f64=OpenAPIUtils.parameterOfOperation(op, "f64").get();
        Assert.assertTrue(name.getSchema().getType().equals("string"));
        Assert.assertTrue(name.getSchema().getFormat()==null);
        
 
        Assert.assertTrue(i32.getSchema().getType().equals("integer"));
        Assert.assertTrue(i32.getSchema().getFormat().equals("int32"));
        
        Assert.assertTrue(i64.getSchema().getType().equals("integer"));
        Assert.assertTrue(i64.getSchema().getFormat().equals("int64"));
        
        Assert.assertTrue(f32.getSchema().getType().equals("number"));
        Assert.assertTrue(f32.getSchema().getFormat().equals("float"));
        
        Assert.assertTrue(f64.getSchema().getType().equals("number"));
        Assert.assertTrue(f64.getSchema().getFormat().equals("double"));
    }
    
    @RestController
    public static class A{
        
        @GetMapping("/a")
        public String echo(
                @RequestParam("name")String name,
                @RequestParam("i32")Integer i32,
                @RequestParam("i64")Long i64,
                @RequestParam("f32")Float f32,
                @RequestParam("f64")Double f64) {
            return "";
        }
    }
}
