package com.hngd.utils;

import java.lang.reflect.Type;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.reflect.TypeToken;
import com.hngd.openapi.TypeResolver;
import com.hngd.parser.source.CommentStore;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class TypeUtilsTest {

    @SuppressWarnings("serial")
    @Test
    public void testMultipart() {
        
        Type parameterType=new TypeToken<MultipartFile[]>() {}.getType();
        Boolean result=TypeUtils.isMultipartType(parameterType);
        Assert.assertTrue(result);
        parameterType=new TypeToken<MultipartFile>() {}.getType();
        result=TypeUtils.isMultipartType(parameterType);
        Assert.assertTrue(result);
    }
    
    @Test
    @SuppressWarnings("serial")
    public void testToRef() {
        
        Type t1=new TypeToken<A1<String>>() {}.getType();
        Type t2=new TypeToken<A2<String,Void>>() {}.getType();
        Type t3=new TypeToken<A3<String,Void,Date>>() {}.getType();
        Type t4=new TypeToken<A3<String,A2<String,Void>,Date>>() {}.getType();
        
        TypeResolver  tr=new TypeResolver(new CommentStore());
        OpenAPI openAPI=new OpenAPI();
        
        String ref1=TypeUtils.toRef(t1);
        String ref11=tr.resolveAsSchema(t1, openAPI);
        Assert.assertEquals(ref11, ref1);
        
        String ref2=TypeUtils.toRef(t2);
        String ref21=tr.resolveAsSchema(t2, openAPI);
        Assert.assertEquals(ref21, ref2);
        
        String ref3=TypeUtils.toRef(t3);
        String ref31=tr.resolveAsSchema(t3, openAPI);
        Assert.assertEquals(ref31, ref3);
        
        String ref4=TypeUtils.toRef(t4);
        String ref41=tr.resolveAsSchema(t4, openAPI);
        Assert.assertEquals(ref41, ref4);
        
        System.out.println(Json.pretty(openAPI));
    }
    
    static class A1<T>{
        T data;
    }
    static class A2<T,R>{
        T data;
        R data1;
    }
    static class A3<T,R,U>{
        T data;
        R data1;
        U data2;
    }
}
