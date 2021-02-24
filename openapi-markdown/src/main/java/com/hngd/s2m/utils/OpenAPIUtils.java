package com.hngd.s2m.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.SimpleType;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class OpenAPIUtils {

    private static final Logger logger=LoggerFactory.getLogger(OpenAPIUtils.class);
    public static final String REF_PREFFIX="#/components/schemas/";
    public static String refToKey(String ref) {
        if(StringUtils.isEmpty(ref)) {
            return ref;
        }
        if(ref.contains(REF_PREFFIX)) {
            return ref.replace(REF_PREFFIX, "");
        }else {
            return ref;
        }
    }
    
    public static OpenAPI loadFromFile(File file) {
        String s=null;
        try {
            s = FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            logger.error("",e);
        }
        SimpleType valueType=ReferenceType.construct(OpenAPI.class);
        OpenAPI openAPI=new OpenAPI();
        try {
            openAPI = Json.mapper().readValue(s, valueType);
        } catch (IOException e) {
            logger.error("",e);
        }
        return openAPI;
    }
}
