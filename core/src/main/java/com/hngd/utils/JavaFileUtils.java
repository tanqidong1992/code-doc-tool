package com.hngd.utils;

import java.io.File;

public class JavaFileUtils {
	 public static boolean isJavaFile(File file) {
	    	return file.getName().endsWith(".java");
	    }
}
