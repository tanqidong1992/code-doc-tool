package com.hngd.openapi;

import java.util.Arrays;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.parser.source.CommentStore;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class RequiredParameterTest {

    public static void main(String[] args) {
        
        OpenAPI openAPI=new OpenAPI();
        CommentStore commentStore=new CommentStore();
        OpenAPITool t=new OpenAPITool(openAPI, commentStore);
        t.parse(Arrays.asList(A.class));
        Json.prettyPrint(openAPI);
    }
    
    @RestController
    @RequestMapping("/a")
    public static class A{
        
        @GetMapping("/echo")
        public String echo(
                @RequestParam(value="requiredWithDefault",required = true,defaultValue = "1")int requiredWithDefault,
                @RequestParam(value="requiredWithoutDefault",required = true)int requiredWithoutDefault,
                @RequestParam(value="requiredWithEmptyDefault",required = true,defaultValue = "")int requiredWithEmptyDefault
                ) {
            return "";
        }
    }
}
