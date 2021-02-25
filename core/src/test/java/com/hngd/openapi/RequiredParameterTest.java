package com.hngd.openapi;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.base.OpenAPIUtils;
import com.hngd.parser.source.CommentStore;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;

public class RequiredParameterTest {

    @Test
    public void test() {
        
        OpenAPI openAPI=new OpenAPI();
        CommentStore commentStore=new CommentStore();
        OpenAPITool t=new OpenAPITool(openAPI, commentStore);
        t.parse(Arrays.asList(A.class));
        Json.prettyPrint(openAPI);
        
        Operation op=OpenAPIUtils.getOperation(openAPI, "/a/echo", "get").get();
        Parameter requiredWithDefault=OpenAPIUtils.parameterOfOperation(op, "requiredWithDefault").get();
        Parameter requiredWithoutDefault=OpenAPIUtils.parameterOfOperation(op, "requiredWithoutDefault").get();
        Parameter requiredWithEmptyDefault=OpenAPIUtils.parameterOfOperation(op, "requiredWithEmptyDefault").get();
        Assert.assertTrue(!requiredWithDefault.getRequired());
        Assert.assertTrue(requiredWithoutDefault.getRequired());
        Assert.assertTrue(!requiredWithEmptyDefault.getRequired());
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
