/**
 * Copyright (c) 2015,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：Config.java
 * @时间：2016年8月12日 上午11:26:43
 * @作者：
 * @备注：
 * @版本:
 */
package org.docgen;

import java.io.File;
import java.util.Arrays;

/**
 * @author 
 */
public class Config
{
    static String controllerRoot = "..\\web\\src\\main\\java\\com\\hngd\\web\\controller";
    static String daoRoot = "..\\dao\\src\\main\\java\\com\\hngd\\dao";
    static String serviceRoot = "..\\service\\src\\main\\java\\com\\hngd\\service";
    
    public static void main(String[] args)
    {
        File file=new File(daoRoot);
        
        Arrays.asList(file.listFiles()).stream()
        .map(f->{
            
            return f.getName();
        }).filter(fileName->{
            
            return fileName.endsWith(".java");
        }).map(fileName->{
            
            return fileName.replace(".java", "");
        }).forEach(simpleClassName->{
            
            System.out.printf("entityNames.put(\""+simpleClassName+"\",\"\");\n");
        });
        
    }
}
