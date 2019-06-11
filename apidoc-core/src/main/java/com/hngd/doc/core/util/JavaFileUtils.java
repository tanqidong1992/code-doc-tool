package com.hngd.doc.core.util;

import java.io.File;

public class JavaFileUtils {
	 public static boolean isJavaFile(File file) {
	    	return file.getName().endsWith(".java");
	    }
}
