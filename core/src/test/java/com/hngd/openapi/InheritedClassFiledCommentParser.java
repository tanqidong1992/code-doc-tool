package com.hngd.openapi;

import java.io.File;
import java.lang.reflect.Type;

import org.junit.Assert;
import org.junit.Test;

import com.hngd.test.dto.Teacher;
import com.hngd.parser.source.SourceParserContext;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

public class InheritedClassFiledCommentParser {

    @Test
    public void test() {
        SourceParserContext pc=new SourceParserContext();
        String rootPath="../core-test/src/main/java";
        File directory=new File(rootPath);
        pc.initSource(directory);
        //CommonClassCommentParser.printResult();
        Type type=Teacher.class;
        OpenAPI openAPI=new OpenAPI();
        TypeResolver tr=new TypeResolver(pc.getCommentStore());
        tr.resolveAsSchema(type, openAPI);
        Json.prettyPrint(openAPI);
        Schema<?> schema=openAPI.getComponents().getSchemas().get("Teacher");
        String commentForName=((Schema<?>)schema.getProperties().get("name")).getDescription();
        Assert.assertTrue(commentForName.equals("姓名"));
        String commentForAge=((Schema<?>)schema.getProperties().get("age")).getDescription();
        Assert.assertTrue(commentForAge.equals("年龄"));

    }

}
