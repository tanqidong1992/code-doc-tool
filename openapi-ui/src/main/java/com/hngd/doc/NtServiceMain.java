/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：NtServiceMain.java
 * @时间：2017年4月1日 上午9:04:44
 * @作者：
 * @备注：
 * @版本:
 */
package com.hngd.doc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
/**
 * @author
 */
public class NtServiceMain {

    // init log
    static {
        File file = new File("./config/log4j2.xml");
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));) {
            final ConfigurationSource source = new ConfigurationSource(in);
            Configurator.initialize(null, source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static final Logger logger = LogManager.getLogger(NtServiceMain.class);
    static ConfigurableApplicationContext mApplicationContext;

    public static boolean isStart() {
        return mApplicationContext.isActive();
    }

    public static void onStop(String[] args) {
        if (logger.isInfoEnabled()) {
            logger.info("stop service....");
        }
        isStoped = true;
        if (mBootstrapThread != null) {
            if (mBootstrapThread.isAlive()) {
                mBootstrapThread.interrupt();
            }
        }
        if (mApplicationContext != null) {
            try {
                mApplicationContext.stop();
            } finally {
                mApplicationContext.close();
            }
        }

    }

    static volatile boolean isStoped = false;
    static volatile Thread mBootstrapThread;

    public static void onStart(String[] args) {

        mBootstrapThread = new Thread(() -> {
            while (!isStoped && mApplicationContext == null) {
                try {
                    mApplicationContext = SpringApplication.run(HnvmnsSwaggerUiApplication.class, args);
                } catch (Exception e) {
                    logger.error("start app...", e);
                }
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    logger.error("start app...", e);
                }
            }
        });
        mBootstrapThread.start();
        if (logger.isInfoEnabled()) {
            logger.info("start service ....");
        }
    }

    public static void init(ApplicationListener<ApplicationEvent> listener) {
        mApplicationContext.addApplicationListener(listener);

    }

    public static void main(String[] args) {
        onStart(args);
    }
}
