package com.hngd.doc.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.doc.core.gen.OpenAPITool;

public class ClassUtils {

	private static final Logger logger=LoggerFactory.getLogger(ClassUtils.class);
	public static List<Class<?>> getClassBelowPacakge(String packageName) {
		String packagePath = packageName.replaceAll("\\.", "/");
		Enumeration<URL> dirs = null;
		try {
			dirs = OpenAPITool.class.getClassLoader().getResources(packagePath);
		} catch (IOException e) {
			logger.error("", e);
		}

		List<Class<?>> clazzs = new LinkedList<>();
		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			File file = new File(url.getFile());
			// 把此目录下的所有文件列出
			String[] fileNames = file.list();
			// 循环此数组，并把.class去掉
			for (String fileName : fileNames) {
				if (!fileName.endsWith(".class")) {
					logger.warn("the file {} is not a class",file.getAbsolutePath() + fileName );
					continue;
				}
				fileName = fileName.substring(0, fileName.length() - 6);
				// 拼接上包名，变成全限定名
				String qName = packageName + "." + fileName;
				// 如有需要，把每个类生实一个实例
				Class<?> cls = null;
				try {
					cls = Class.forName(qName);
				} catch (ClassNotFoundException e) {
					logger.error("", e);
				}
				if (cls != null) {
					clazzs.add(cls);
				}
			}
		}
		return clazzs;
	}
}
