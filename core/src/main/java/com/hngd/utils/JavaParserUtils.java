package com.hngd.utils;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class JavaParserUtils {

	public static String getParentNodeNameList(Node node) {
		Optional<Node> optionalParentNode=node.getParentNode();
		String parentNames=null;
		if(optionalParentNode.isPresent()) {
			parentNames=getParentNodeNameList(optionalParentNode.get());
		}
		String name="";
		if(node instanceof ClassOrInterfaceDeclaration) {
			name=((ClassOrInterfaceDeclaration)node).getNameAsString();
		}else if(node instanceof FieldDeclaration) {
			//TODO to spport multi variable?
			name=((FieldDeclaration)node).getVariables().get(0).getNameAsString();
		}else if(node instanceof MethodDeclaration) {
			name=((MethodDeclaration)node).getNameAsString();
		}
		if(StringUtils.isEmpty(name)) {
			return "";
		}
		if(StringUtils.isNotBlank(parentNames)) {
		    return parentNames+"."+name;
		}else {
			return name;
		}
	}
}
