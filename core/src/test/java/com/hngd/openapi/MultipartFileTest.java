package com.hngd.openapi;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.base.OpenAPIUtils;
import com.hngd.constant.Constants;
import com.hngd.openapi.RequestBodyAndParameterTest.A;
import com.hngd.parser.source.CommentStore;
import com.hngd.parser.source.SourceParserContext;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;

public class MultipartFileTest {

    @Test
    public void testMultipartFileWithoutAnnotation() {
        
        SourceParserContext c=new SourceParserContext();
        c.initJavaSourceFile(new File("src/test/java/com/hngd/openapi/MultipartFileTest.java"));
        
        OpenAPI openAPI = new OpenAPI();
        CommentStore commentStore = c.getCommentStore();
        OpenAPITool t = new OpenAPITool(openAPI, commentStore);
        t.parse(Arrays.asList(MfController.class));
        commentStore.print();
        Json.prettyPrint(openAPI);
        Operation mf = OpenAPIUtils.getOperation(openAPI, "/mf", "post").get();
        
        MediaType mt = mf.getRequestBody().getContent().get(Constants.MULTIPART_FORM_DATA);
        Assert.assertTrue(mt.getSchema().getProperties().containsKey("file1"));
        Schema<?> schema=(Schema<?>) mt.getSchema().getProperties().get("file1");
        Assert.assertEquals("string", schema.getType());
        Assert.assertEquals("binary", schema.getFormat());
        
        
        
        mf = OpenAPIUtils.getOperation(openAPI, "/mf/array", "post").get();
        
        mt = mf.getRequestBody().getContent().get(Constants.MULTIPART_FORM_DATA);
        Assert.assertTrue(mt.getSchema().getProperties().containsKey("files"));
        Schema<?> arrayschema=(Schema<?>) mt.getSchema().getProperties().get("files");
        Assert.assertEquals("array", arrayschema.getType());
        schema=((ArraySchema)arrayschema).getItems();
        Assert.assertEquals("string", schema.getType());
        Assert.assertEquals("binary", schema.getFormat());
        
        
        Operation mfWithParams = OpenAPIUtils.getOperation(openAPI, "/mf/with/params/{id}", "post").get();
        
        mt = mfWithParams.getRequestBody().getContent().get(Constants.MULTIPART_FORM_DATA);
        Assert.assertTrue(mt.getSchema().getProperties().containsKey("file"));
         
        schema=(Schema<?>) mt.getSchema().getProperties().get("file");
        Assert.assertEquals("string", schema.getType());
        Assert.assertEquals("binary", schema.getFormat());
        
        Assert.assertTrue(mt.getSchema().getProperties().containsKey("name"));
        
        Assert.assertTrue(!mt.getSchema().getProperties().containsKey("id"));
        Assert.assertTrue(!mt.getSchema().getProperties().containsKey("user"));
        
        Assert.assertTrue(!mt.getEncoding().containsKey("id"));
        Assert.assertTrue(!mt.getEncoding().containsKey("user"));
        
        
        Parameter user=OpenAPIUtils.parameterOfOperation(mfWithParams, "user").get();
        Assert.assertEquals("cookie", user.getIn());
        //Parameter name=OpenAPIUtils.parameterOfOperation(mfWithParams, "name").get();
        //Assert.assertEquals("query", name.getIn());
        Parameter id=OpenAPIUtils.parameterOfOperation(mfWithParams, "id").get();
        Assert.assertEquals("path", id.getIn());
        
        
    }
    /**
     * mf
     * @author tqd
     *
     */
    @RestController
    public class MfController {
 
        /**
         * b
         * @param id
         * @param user
         * @param name
         * @param file
         * @return
         */
        @PostMapping("/mf/with/params/{id}")
        public String mfWithParams(
                @PathVariable("id")String id,
                @CookieValue("user")String user,
                @RequestParam("name")String name,
                @RequestPart("file")MultipartFile file) {
            return "param[name]:"+name+",cookit[user]"+user;
        }
        /**
         * mf test
         * @param file1 f
         * @return
         */
        @PostMapping("/mf")
        public String get3(MultipartFile file1) {
            return "filename:"+file1.getOriginalFilename();
        }
        /**
         * b
         * @param files file array
         * @return
         */
        @PostMapping("/mf/array")
        public String get4(MultipartFile[] files) {
            return "file size:"+files.length;
        }
        
    }
}
