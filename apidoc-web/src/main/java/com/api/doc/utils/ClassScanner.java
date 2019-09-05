/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：ClassScanner.java
 * @时间：2017年2月28日 上午10:47:23
 * @作者：
 * @备注：
 * @版本:
 */

package com.api.doc.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.stereotype.Controller;

/**
 * @author
 */
public class ClassScanner {

	/**
	 * @param jfile
	 * @author
	 * @since 1.0.0
	 * @time 2017年2月28日 上午10:59:24
	 */
	public static void showJarFile(JarFile jfile) {
		Enumeration<JarEntry> entries = jfile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			if (entryName.endsWith(".class")) {
				// System.out.println(entry.getName());
				resoveClass(jfile.getName(), entry.getName());
			}
		}
	}

	/**
	 * @param classpath
	 * @author
	 * @since 1.0.0
	 * @time 2017年2月28日 上午10:50:10
	 */
	public static void showChildren(String rootClasspath, File classpath) {
		String fileName = classpath.getAbsolutePath();
		if (fileName.endsWith(".class")) {
			fileName = fileName.replaceFirst(rootClasspath, "");
			// System.out.println(fileName);
			resoveClass(rootClasspath, fileName);
		}
		if (classpath.isDirectory()) {
			File files[] = classpath.listFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					showChildren(rootClasspath, file);
				}
			}
		}
	}

	/**
	 * @param fileName
	 * @author
	 * @since 1.0.0
	 * @time 2017年2月28日 上午11:13:33
	 */
	private static void resoveClass(String rootPath, String fileName) {
		String className = fileName.replace(".class", "");
		if (className.contains("\\")) {
			className = className.replace("\\", ".");
		}
		if (className.contains("/")) {
			className = className.replace("/", ".");
		}
		// System.out.println(className);
		Class<?> clazz = null;
		try {

			clazz = Class.forName(className, false, ClassScanner.class.getClassLoader());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (clazz != null) {
			Controller controller = clazz.getAnnotation(Controller.class);
			System.out.println(clazz.getName());
		}
	}
}
