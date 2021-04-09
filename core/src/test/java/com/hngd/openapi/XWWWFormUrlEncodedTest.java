package com.hngd.openapi;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.base.OpenAPIUtils;
import com.hngd.constant.Constants;
import com.hngd.openapi.MultipartFileTest.MfController;
import com.hngd.parser.source.CommentStore;
import com.hngd.parser.source.SourceParserContext;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
 
public class XWWWFormUrlEncodedTest {

    
    @Test
    public void test() {
        SourceParserContext cp=new SourceParserContext();
        cp.initJavaSourceFile(new File("src/test/java/com/hngd/openapi/XWWWFormUrlEncodedTest.java"));
        
        OpenAPI openAPI = new OpenAPI();
        CommentStore commentStore = cp.getCommentStore();
        OpenAPITool t = new OpenAPITool(openAPI, commentStore);
        t.parse(Arrays.asList(FormUrlencodedController.class));
        commentStore.print();
        Json.prettyPrint(openAPI);
        Operation mf = OpenAPIUtils.getOperation(openAPI, "/form", "post").get();
        //Parameter a=OpenAPIUtils.parameterOfOperation(mf, "a").get();
        //Parameter b=OpenAPIUtils.parameterOfOperation(mf, "b").get();
        //Parameter c=OpenAPIUtils.parameterOfOperation(mf, "c").get();
        //Assert.assertEquals("query", a.getIn());
        //Assert.assertEquals("query", b.getIn());
        //Assert.assertEquals("query", c.getIn());
        MediaType formUrlEncoded = mf.getRequestBody().getContent().get(Constants.APPLICATION_FORM_URLENCODED_VALUE);
        Assert.assertTrue(formUrlEncoded.getSchema().getProperties().containsKey("a"));
        Assert.assertTrue(formUrlEncoded.getSchema().getProperties().containsKey("b"));
        Assert.assertTrue(formUrlEncoded.getSchema().getProperties().containsKey("c"));
    }
    
    /**
     * b
     * @author tqd
     *
     */
    @RestController
    public class FormUrlencodedController {

        /**
         * a
         * @param a a x
         * @param b b c
         * @param c c d
         * @return
         */
        @PostMapping("/form")
        public String form(String a,String b,@RequestParam("c")String c) {
            return "a:"+a+";b:"+b+";c:"+c;
        }
    }
}
