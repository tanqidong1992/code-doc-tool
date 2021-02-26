package com.hngd.utils;

import java.io.File;

public class JavaFileUtils {
    
     public static boolean isJavaSourceFile(File file){
         return file.getName().endsWith(".java");
     }
}
