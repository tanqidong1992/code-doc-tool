package com.hngd.doc;

import java.io.File;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hngd.doc.swagger.TagFilter;

import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

public class FilterTest {

    public static void main(String[] args) throws JsonProcessingException {
        
        File file=new File("./data/test.json");
        String tag="设备检验检测记录管理";
        OpenAPI openAPI=SwaggerFileLoader.loadFromFile(file);
        TagFilter tagFilter=new TagFilter(tag);
        SpecFilter filter=new SpecFilter();
        OpenAPI openAPI1=filter.filter(openAPI, tagFilter, null, null, null);
        String key="RestResponseListGetCheckRecordDetails";
        Schema schema0=openAPI.getComponents().getSchemas().get(key);
        Schema schema1=openAPI1.getComponents().getSchemas().get(key);
        String s0=Json.pretty(schema0);
        System.out.println(s0); 
        String s=Json.pretty(schema1);
        System.out.println(s); 

    }

}
