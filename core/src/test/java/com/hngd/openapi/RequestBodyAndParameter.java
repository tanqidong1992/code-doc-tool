package com.hngd.openapi;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.base.OpenAPIUtils;
import com.hngd.constant.Constants;
import com.hngd.parser.source.CommentStore;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.Data;

public class RequestBodyAndParameter {

    @Test
    public void testRequestBodyWithParameters() {
        OpenAPI openAPI=new OpenAPI();
        CommentStore commentStore=new CommentStore();
        OpenAPITool t=new OpenAPITool(openAPI, commentStore);
        t.parse(Arrays.asList(A.class));
        Json.prettyPrint(openAPI);
        Operation op=OpenAPIUtils.getOperation(openAPI, "/a/{area}", "post").get();
        Parameter area=OpenAPIUtils.parameterOfOperation(op, "area").get();
        Assert.assertTrue(area.getIn().equals("path"));
        MediaType mt=op.getRequestBody().getContent().get(Constants.DEFAULT_PRODUCE_TYPE);
        Assert.assertTrue(mt.getSchema().getProperties().containsKey("name"));
        Assert.assertTrue(mt.getSchema().getProperties().containsKey("age"));
        
        Parameter name=OpenAPIUtils.parameterOfOperation(op, "name").get();
        Parameter age=OpenAPIUtils.parameterOfOperation(op, "age").get();
        Parameter clazz=OpenAPIUtils.parameterOfOperation(op, "class").get();
        Assert.assertTrue(name.getIn().equals("query"));
        Assert.assertTrue(age.getIn().equals("query"));
        Assert.assertTrue(clazz.getIn().equals("query"));
    }
    
    @RestController
    @RequestMapping("/a")
    public static class A{
        
        @PostMapping("/{area}")
        public String echo(User u1,@RequestParam("class")String clazz,@RequestBody User user,@PathVariable("area")String area) {
            return "";
        }
    }
    
    @Test
    public void testRequestBody() {
        OpenAPI openAPI=new OpenAPI();
        CommentStore commentStore=new CommentStore();
        OpenAPITool t=new OpenAPITool(openAPI, commentStore);
        t.parse(Arrays.asList(B.class));
        Json.prettyPrint(openAPI);

        Operation op=OpenAPIUtils.getOperation(openAPI, "/b", "post").get();
        Assert.assertTrue(op.getParameters().isEmpty());
        io.swagger.v3.oas.models.parameters.RequestBody body=op.getRequestBody();
        MediaType mt=body.getContent().get(Constants.DEFAULT_PRODUCE_TYPE);
        Schema<?> s=mt.getSchema();
        Assert.assertTrue(s.getProperties().containsKey("name"));
        Assert.assertTrue(s.getProperties().containsKey("age"));
    }


    @RestController
    public static class B{

        @PostMapping("/b")
        public String echo(@RequestBody User user) {
            return "";
        }
    }

    @Data
    static class User{
        public String name;
        public Integer age;
    }
}
