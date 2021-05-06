package com.hngd.doc.utils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.hngd.doc.constants.Constants;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class OpenAPIFileUtils {
    private static final Logger logger=LoggerFactory.getLogger(OpenAPIFileUtils.class);
    
    public static Optional<OpenAPI> loadFromFile(File file) {
        String s=null;
        try {
            s = FileUtils.readFileToString(file, Constants.DEFAULT_CHARSET_NAME);
        } catch (IOException e) {
            logger.error("",e);
        }
        if(s==null) {
            return Optional.empty();
        }
        JavaType valueType=TypeFactory.defaultInstance()
                .constructType(OpenAPI.class);
        OpenAPI openAPI=new OpenAPI();
        try {
            openAPI = Json.mapper().readValue(s, valueType);
        } catch (IOException e) {
            logger.error("",e);
        }
        return Optional.ofNullable(openAPI);
    }
    
}
