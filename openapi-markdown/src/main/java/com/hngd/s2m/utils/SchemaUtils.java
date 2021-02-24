package com.hngd.s2m.utils;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;

public class SchemaUtils {

    public static String schemaKey(Schema schema) {
        
        String s="empty";
        if(schema!=null) {
            StringBuilder sb=new StringBuilder();
            Map<Object,Object> map=schema.getProperties();
            if(map!=null) {
            map.forEach((k,v)->{
                sb.append(k);
            });
            s=sb.toString();
            }
        }
        MessageDigest digest=DigestUtils.getDigest(MessageDigestAlgorithms.SHA_256);
        byte[]data= digest.digest(s.getBytes());
        return Base64.getEncoder().encodeToString(data);
    }
}
