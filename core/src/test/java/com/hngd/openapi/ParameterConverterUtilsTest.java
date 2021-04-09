package com.hngd.openapi;

import java.lang.reflect.Type;

import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.openapi.entity.HttpParameter;
import com.hngd.parser.entity.ClassInfo;

import io.swagger.v3.core.util.Json;

public class ParameterConverterUtilsTest {

    @Test
    public void test() {
        ClassInfo[] c=new ClassInfo[10];
        HttpParameter pc=new HttpParameter();
        Class type=c.getClass();
        System.out.println(type.isArray()+":"+type.getComponentType());
        ParameterConverterUtils.resolveParameterInfo(pc, type);
        System.out.println(Json.pretty(pc));
    }
}
