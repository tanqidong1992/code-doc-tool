package com.hngd.parser.source;

import java.util.Objects;

import com.hngd.parser.entity.ClassInfo;
import com.hngd.parser.entity.FieldInfo;
import com.hngd.parser.entity.MethodInfo;

import lombok.Data;

@Data
public class FileVisitorContext {
	private String packageName;
	
	private FileParseResult parseResult=new FileParseResult();
 
	public FileVisitorContext() {
		
	}
	public FileVisitorContext(String packageName) {
		this.packageName=packageName;
	}
	
	public void saveClassComment(String fullClassName,ClassInfo ci) {
		Objects.requireNonNull(fullClassName);
		Objects.requireNonNull(ci);
		parseResult.getClassComments().put(fullClassName, ci);
	}
	public void saveMethodComment(String fullMethodName, MethodInfo mi) {
		Objects.requireNonNull(fullMethodName);
		Objects.requireNonNull(mi);
		parseResult.getMethodComments().put(fullMethodName, mi);
	}
	public void saveFieldComment(String fullFieldName, FieldInfo fieldInfo) {
		Objects.requireNonNull(fullFieldName);
		Objects.requireNonNull(fieldInfo);
		parseResult.getFieldComments().put(fullFieldName, fieldInfo);
	}
	
}
