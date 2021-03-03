/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：ServerConfig.java
 * @时间：2017年7月7日 上午11:50:27
 * @作者：
 * @备注：
 * @版本:
 */
package com.hngd.openapi.config;

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
import com.hngd.openapi.constant.Constant;

import io.swagger.util.Json;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/**
 * openapi配置
 * @author tqd
 */
public class ServerConfig {
    public List<Server> servers;
    public Info info;
    private static final Logger logger=LoggerFactory.getLogger(ServerConfig.class);
    public static Optional<ServerConfig> load(String filePath) {
         
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
        String src = new String(data, Charset.forName(Constant.DEFAULT_CHARSET_NAME));
        ServerConfig config=null;
        try {
            config= Json.mapper().readValue(src, ServerConfig.class);
        } catch (JsonProcessingException e) {
            logger.error("",e);
        }
        return Optional.ofNullable(config);
    }

}
