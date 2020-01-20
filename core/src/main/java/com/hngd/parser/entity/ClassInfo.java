package com.hngd.parser.entity;

import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *类变量信息
 * @author hnoe-dev-tqd
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ClassInfo extends CommentDecoratedTarget{
 
	/**
	 * 变量声明代码
	 */
	private ClassOrInterfaceDeclaration classOrInterfaceDetail;
	
}
