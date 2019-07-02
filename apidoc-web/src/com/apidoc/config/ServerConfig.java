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
package com.apidoc.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.doc.constant.Constant;
import com.google.gson.Gson;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/**
 * swagger配置
 * @author
 */
public class ServerConfig {
	public List<Server> servers;
	public Info info;
	private static final Logger logger=LoggerFactory.getLogger(ServerConfig.class);
	public static ServerConfig load(String filePath) {
		Gson gson = new Gson();
		File file = new File(filePath);
		Path path = file.toPath();
		byte[] data = null;
		try {
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			logger.error("",e);
		}
		if (data == null) {
			return null;
		}
		String src = new String(data, Charset.forName(Constant.DEFAULT_CHARSET_NAME));
		return gson.fromJson(src, ServerConfig.class);
	}

}
