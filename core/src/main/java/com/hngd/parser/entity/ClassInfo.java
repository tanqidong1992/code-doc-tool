package com.hngd.parser.entity;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *类信息
 * @author tqd
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ClassInfo extends CommentDecoratedTarget{
	/**
	 * 类声明代码
	 */
	private ClassOrInterfaceDeclaration classOrInterfaceDetail;
	
}