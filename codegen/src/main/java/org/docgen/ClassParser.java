package org.docgen;

import java.io.File;
import java.io.IOException;
import java.util.List;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.SourcesHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.comments.Comment;
import japa.parser.ast.comments.JavadocComment;
import japa.parser.ast.type.Type;

public class ClassParser {

	 
public static void parser(File file) {
		
		SourcesHelper sh=new SourcesHelper();
		CompilationUnit cu = null;
		try {
			cu = JavaParser.parse(file);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Comment> comments=cu.getComments();
		List<Node> nodes=cu.getChildrenNodes();
		nodes.forEach(n->{
			
			readNode(n);
			
		});
		comments.forEach(c->{
			
			System.out.println(c.getData());
		});
	}

	public static void parser(String fileName) {
		
	 
			parser(new File(fileName));
	 
	}
	private static void readNode(Node n) {
		// TODO Auto-generated method stub
		 
		if(n instanceof ClassOrInterfaceDeclaration){
			
			ClassOrInterfaceDeclaration classInfo=(ClassOrInterfaceDeclaration) n;
			List<Node> classFields=classInfo.getChildrenNodes();
			classFields.stream().filter(f->{
				
				if(f instanceof MethodDeclaration){
					
					return true;
				}else{
					
					return false;
				}
 
			}).forEach(f->{
				
				MethodDeclaration method=(MethodDeclaration) f;
				JavadocComment comment=(JavadocComment) method.getComment();
			 
				System.out.println(method.getName());;
				List<Parameter> parameters=method.getParameters();
				 parameters.forEach(p->{
					 
					 System.out.println(p.getId()+":"+p.getType());
				 });
				Type returnType=method.getType();
				 System.out.println(returnType);
			});;
			
		}
	}
}
