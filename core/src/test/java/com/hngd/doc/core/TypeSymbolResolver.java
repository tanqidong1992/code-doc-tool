package com.hngd.doc.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.hngd.utils.ClassUtils;

public class TypeSymbolResolver {

	public static void main(String[] args) throws FileNotFoundException {
		
		String filePath="../core-test/src/main/java/com/hngd/web/controller/RoleController.java";
		File srcDir=new File("../core-test/src/main/java");
		 
		 
		CompilationUnit	cu = ClassUtils.parseClass(new File(filePath));
		 
		List<Node>  nodes=getChild(cu);
		nodes.stream()
		 .filter(node->{
			 if(node instanceof Type){
				 return true;
			 }
			 return false;
		 })
		.forEach(node->{
			 
			System.out.println(node);
		});
	}
	
	public static List<Node> getChild(Node node){
		LinkedList<Node> nodes=new LinkedList<>();
		List<Node> children=node.getChildNodes();
		if(children.size()>0){
			nodes.addAll(children);
			children.forEach(child->{
				nodes.addAll(getChild(child));
			});
		}
		return nodes;
	}
}
