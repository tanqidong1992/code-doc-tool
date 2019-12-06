package com.hngd.parser.entity;

import java.util.List;

import com.github.javaparser.ast.body.FieldDeclaration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *类成员变量信息
 * @author hnoe-dev-tqd
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class FieldInfo extends BaseInfo{

	public FieldInfo(String comment, String fieldName, FieldDeclaration fieldDetail) {
		 
		this.setComment(comment)
		.setName(fieldName);
		this.fieldDetail = fieldDetail;
	}
	/**
	 * 变量声明代码
	 */
	private FieldDeclaration fieldDetail;
	
}
