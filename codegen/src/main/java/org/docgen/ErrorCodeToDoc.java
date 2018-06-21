/**
 * Copyright (c) 2015,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：ErrorCodeTools.java
 * @时间：2016年7月29日 下午2:26:47
 * @作者：tqd
 * @备注：
 * @版本:
 */
package org.docgen;

import com.hngd.entity.ErrorCode;

/**
 * @author tqd
 */
public class ErrorCodeToDoc
{
    public static void main(String[] args)
    {
        System.out.println("|错误码|描述|");
        System.out.println("|:--|:--|");
        
        ErrorCode.errorDescriptions.keySet().stream().sorted((l,r)->{
            
            int retValue=0;
            if(l>r){
                
                retValue= 1;
            }else if(l==r){
                
                retValue=  0;
            }else{
                
                retValue=  -1;
            }
            return retValue*-1;
            
        }).forEach(key->{
            
            System.out.println("|"+key+"|"+ErrorCode.errorDescriptions.get(key)+"|");
        });
    }
}
