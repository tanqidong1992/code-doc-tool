package com.hngd.parser.entity;

import com.github.javaparser.ast.body.FieldDeclaration;

/**
 *     类成员变量信息
 * @author hnoe-dev-tqd
 *
 */
public class FieldInfo {

	public FieldInfo(String comment, String fieldName, FieldDeclaration fieldDetail) {
		 
		this.comment = comment;
		this.fieldName = fieldName;
		this.fieldDetail = fieldDetail;
	}
	/**
	 * 注释
	 */
	public String comment;
	/**
	 * 变量名称
	 */
	public String fieldName;
	/**
	 * 变量声明代码
	 */
	public FieldDeclaration fieldDetail;
	
}
