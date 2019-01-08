/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：UpdateThread.java
 * @时间：2017年2月14日 上午9:37:19
 * @作者：
 * @备注：
 * @版本:
 */

package com.apidoc.servlet;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apidoc.server.ServerMain;
import com.apidoc.utils.DirectoryWatcher;
import com.apidoc.utils.DirectoryWatcher.FileListener;
import com.hngd.webapi.doc.App;

/**
 * @author
 */
public class UpdateThread extends Thread
{
    private FileListener listener = new FileListener()
    {
        @Override
        public void onFileChange()
        {
            try
            {
                App.init(ServerMain.config);
            } catch (Exception e)
            {
                logger.error("", e);
            }
        }
    };
    static final Logger  logger   = LoggerFactory.getLogger(UpdateThread.class);
    private String[]     mWatchDirectories;
    private Thread[]     mThreads;

    public UpdateThread(String[] watchDirectories)
    {
        if (watchDirectories == null || watchDirectories.length <= 0)
        {
            throw new RuntimeException("watchDirectories is empty");
        }
        mWatchDirectories = watchDirectories;
        mThreads = new Thread[mWatchDirectories.length];
        for (int i = 0; i < mWatchDirectories.length; i++)
        {
            final String watchDirectory = mWatchDirectories[i];
            mThreads[i] = new Thread(() ->
            {
                try
                {
                    @SuppressWarnings("unused")
                    DirectoryWatcher dw = new DirectoryWatcher(watchDirectory, listener);
                } catch (Exception e)
                {
                    logger.error("", e);
                }
            });
        }
    }

    public void start()
    {
        for (int i = 0; i < mThreads.length; i++)
        {
            mThreads[i].start();
        }
    }
}
