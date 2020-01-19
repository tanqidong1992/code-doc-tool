package com.hngd.parser.source;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hngd.parser.entity.FieldInfo;
import com.hngd.parser.entity.MethodInfo;

import lombok.Data;

@Data
public class FileParseResult {

	private String fileSha2;
	private  Map<String, String> classComments = new ConcurrentHashMap<>();
	private  Map<String, MethodInfo> methodComments = new ConcurrentHashMap<>();
	private  Map<String, FieldInfo> fieldComments = new ConcurrentHashMap<>();
}
