package com.hngd.tool.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hngd.constant.Constants;

import io.swagger.util.Json;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/**
 * openapi配置
 * @author tqd
 */
public class OpenAPIConfig {
    public List<Server> servers;
    public Info info;
    private static final Logger logger=LoggerFactory.getLogger(OpenAPIConfig.class);
    public static Optional<OpenAPIConfig> load(String filePath) {
        File file = new File(filePath);
        Path path = file.toPath();
        byte[] data = null;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            logger.error("",e);
        }
        if (data == null) {
            return Optional.empty();
        }
        String src = new String(data, Charset.forName(Constants.DEFAULT_CHARSET_NAME));
        OpenAPIConfig config=null;
        try {
            config= Json.mapper().readValue(src, OpenAPIConfig.class);
        } catch (JsonProcessingException e) {
            logger.error("",e);
        }
        return Optional.ofNullable(config);
    }
}
