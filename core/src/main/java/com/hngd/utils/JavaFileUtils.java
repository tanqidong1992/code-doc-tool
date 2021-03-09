package com.hngd.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class JavaFileUtils {
    
     public static boolean isJavaSourceFile(File file){
         return file.getName().endsWith(".java");
     }
     
     public static byte[] sha2(byte[] fileContent) {
        
        MessageDigest md=null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return md.digest(fileContent);
     }
}
