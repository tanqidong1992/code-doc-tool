package com.hngd.parser.source;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hngd.parser.entity.ClassInfo;
import com.hngd.parser.entity.FieldInfo;
import com.hngd.parser.entity.MethodInfo;

import lombok.Data;

@Data
public class SourceParseResult implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String fileSha2;
    private  Map<String, ClassInfo> classComments = new ConcurrentHashMap<>();
    private  Map<String, MethodInfo> methodComments = new ConcurrentHashMap<>();
    private  Map<String, FieldInfo> fieldComments = new ConcurrentHashMap<>();
}
