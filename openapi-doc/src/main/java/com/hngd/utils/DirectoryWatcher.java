/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：DirectoryWatcher.java
 * @时间：2017年2月14日 上午9:38:59
 * @作者：
 * @备注：
 * @版本:
 */

package com.hngd.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tqd
 */
public class DirectoryWatcher {

	/**
	 * 
	 * @author hnoe-dev-tqd
	 *
	 */
	public interface FileListener {
		/**
		 * 目录下文件变化
		 */
		void onFileChange();
	}

	static final Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);
	String mFilePath;
	FileListener mListener;

	public DirectoryWatcher(String mFilePath, FileListener listener) throws IOException {
		mListener = listener;
		this.mFilePath = mFilePath;
		File file = new File(mFilePath);
		if (file.exists() && file.isDirectory()) {
			doWatch(file);
		} else {
			throw new RuntimeException("the file[" + mFilePath + "] is not exist or is not a directory");
		}

	}
	
	private void doWatch(File file) throws IOException {
		Path path = file.toPath();
		FileSystem fileSystem = FileSystems.getDefault();
		WatchService watchService = fileSystem.newWatchService();
		path.register(watchService, 
				StandardWatchEventKinds.ENTRY_CREATE, 
				StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.OVERFLOW);
		for (;;) {
			WatchKey watchKey = null;
			try {
				watchKey = watchService.take();
			} catch (InterruptedException e) {
				logger.error("", e);
				break;
			}
			if (watchKey == null) {
				continue;
			}
			List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
			if (watchEvents == null) {
				continue;
			}
			for (WatchEvent<?> event : watchEvents) {
				Kind<?> kind = event.kind();
				Object context = event.context();
				if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind)) {
					logger.info("create " + context);
					mListener.onFileChange();
				} else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(kind)) {
					logger.info("modify " + context);
					mListener.onFileChange();
				} else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind)) {
					logger.info("delete " + context);
					mListener.onFileChange();
				} else if (StandardWatchEventKinds.OVERFLOW.equals(kind)) {
					logger.info("OVERFLOW " + context);
					mListener.onFileChange();
				} else {
					logger.info("unknow " + context);
					mListener.onFileChange();
				}
			}
			boolean valid = watchKey.reset();
			if (!valid) {
				logger.error("the watchKey is invalid");
				break;
			}
		}
	}

}
