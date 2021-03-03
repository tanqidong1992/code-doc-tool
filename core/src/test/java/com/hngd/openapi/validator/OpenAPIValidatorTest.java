package com.hngd.openapi.validator;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.hngd.constant.Constants;

import io.swagger.v3.core.util.Json;

public class OpenAPIValidatorTest {

    @Test
    public void test() {
        OpenAPIValidator oav=new OpenAPIValidator();
        String s="{}";
        ValidationResponse vr=oav.validate(s);
        //System.out.println(Json.pretty(vr));
    }
    
    @Test
    public void test1() throws IOException {
        OpenAPIValidator oav=new OpenAPIValidator();
        String s=FileUtils.readFileToString(new File("./test-data/api.json"), Constants.DEFAULT_CHARSET);
        ValidationResponse vr=oav.validate(s);
        System.out.println(Json.pretty(vr));
    }
}
