package com.hngd.openapi;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.base.OpenAPIUtils;
import com.hngd.parser.source.CommentStore;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Data;

public class MultipartParameterTest {

    @Test
    public void testSingleFile() {
        OpenAPI openAPI=new OpenAPI();
        CommentStore commentStore=new CommentStore();
        OpenAPITool t=new OpenAPITool(openAPI, commentStore);
        t.parse(Arrays.asList(A.class));
        Json.prettyPrint(openAPI);
        Operation op=OpenAPIUtils.getOperation(openAPI, "/a", "post").get();
        MediaType mt=op.getRequestBody().getContent().get("multipart/form-data");
        Assert.assertTrue(mt!=null);
        Schema<?> file=(Schema<?>) mt.getSchema().getProperties().get("file");
        Assert.assertTrue(file.getType().equals("string"));
        Assert.assertTrue(file.getFormat().equals("binary"));
         
    }

    @RestController
    public static class A{
        
        @PostMapping("/a")
        public String echo(
                @RequestPart("user") User user,
                @RequestPart("file")MultipartFile file ) {
            return "";
        }
    }
    
    
    @Test
    public void testFileArray() {
        OpenAPI openAPI=new OpenAPI();
        CommentStore commentStore=new CommentStore();
        OpenAPITool t=new OpenAPITool(openAPI, commentStore);
        t.parse(Arrays.asList(B.class));
        Json.prettyPrint(openAPI);
        Operation op=OpenAPIUtils.getOperation(openAPI, "/b", "post").get();
        MediaType mt=op.getRequestBody().getContent().get("multipart/form-data");
        Assert.assertTrue(mt!=null);
        ArraySchema files=(ArraySchema) mt.getSchema().getProperties().get("files");
        Schema<?> file=files.getItems();
        Assert.assertTrue(file.getType().equals("string"));
        Assert.assertTrue(file.getFormat().equals("binary"));
         
    }
    
    
    
    @RestController
    public static class B{
        
        @PostMapping("/b")
        public String echo(
                @RequestPart("user") String user,
                @RequestPart("files")MultipartFile[] file ) {
            return "";
        }
    }
     
    @Data
    static class User{
        public String name;
        public Integer age;
    }
}
