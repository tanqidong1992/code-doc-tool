package com.hngd.doc.core;

import com.github.javaparser.ast.body.FieldDeclaration;

public class FieldInfo {

	
	public FieldInfo(String comment, String fieldName, FieldDeclaration fieldDetail) {
		 
		this.comment = comment;
		this.fieldName = fieldName;
		this.fieldDetail = fieldDetail;
	}
	public String comment;
	public String fieldName;
	public FieldDeclaration fieldDetail;
}
