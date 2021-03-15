package com.hngd.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaFileUtils {
     
    private static final Logger logger=LoggerFactory.getLogger(JavaFileUtils.class);
     
     public static boolean isJavaSourceFile(File file){
         return file.isFile() && file.getName().endsWith(".java");
     }
     
     public static byte[] sha2(byte[] fileContent) {
        
        MessageDigest md=null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            logger.error("",e);
        }
        
        return md.digest(fileContent);
     }
}
