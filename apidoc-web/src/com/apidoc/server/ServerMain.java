/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：ServerMain.java
 * @时间：2017年2月14日 上午8:41:24
 * @作者：
 * @备注：
 * @版本:
 */

package com.apidoc.server;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.apidoc.config.ServerConfig;
import com.apidoc.servlet.App;
import com.apidoc.servlet.MainServlet;
import com.apidoc.servlet.UpdateThread;
import com.google.common.io.Files;

/**
 * @author
 */
public class ServerMain {
	static {
		PropertyConfigurator.configure("./log4j.properties");
	}
	private static final Logger logger = Logger.getLogger(ServerMain.class);
    public static final ServerConfig config=ServerConfig.load("./conf/app.json");
	static {
		
		try {
			App.init(config);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
			logger.error("", e);
		}
	}

	public static void main(String[] args) throws Exception {
		// Files.write(App.toJson(), new File(API_FILE_PATH),
		// Charset.forName("UTF-8"));
		UpdateThread ut = new UpdateThread(App.getAllSrcDir());
		ut.start();
		Server server = new Server(config.server.port);
		ServletHandler servletHandler = new ServletHandler();
		servletHandler.setServer(server);
		servletHandler.addServletWithMapping(MainServlet.class, "/api");
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase("./WebContent");
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		server.setHandler(servletHandler);
		servletHandler.setHandler(resourceHandler);
		server.start();
		server.join();
	}
}
