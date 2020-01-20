package com.hngd.parser.source;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.hngd.exception.SourceParseException;
import com.hngd.parser.entity.ClassInfo;
import com.hngd.parser.entity.FieldInfo;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.javadoc.BlockTag;
import com.hngd.parser.javadoc.Description;
import com.hngd.parser.javadoc.JavaDocCommentElement;
import com.hngd.parser.javadoc.JavaDocCommentParser;
import com.hngd.utils.JavaParserUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MySourceVisitor extends VoidVisitorAdapter<FileVisitorContext>{
    @Override
    public void visit(ClassOrInterfaceDeclaration classOrInterface, FileVisitorContext context) {
    	//String name=JavaParserUtils.getParentNodeNameList(classOrInterface);
    	//System.out.println("visit class:"+context.getPackageName()+"."+name);
    	parseClassComment(classOrInterface,context);
    	classOrInterface.getChildNodes().stream()
    	    .filter(n->filterAndParseFieldComment(n,context))
		    .filter(n -> n instanceof MethodDeclaration)
		    .map(MethodDeclaration.class::cast)
		    .forEach(m->parseMethodComment(m,context));
    	
    	super.visit(classOrInterface, context);
    }
    
    public static boolean parseClassComment(ClassOrInterfaceDeclaration classOrInterfaceDeclaration,FileVisitorContext context){
		Comment comment = classOrInterfaceDeclaration.getComment().orElse(null);
		String classOrInterfaceName = classOrInterfaceDeclaration.getName().asString();
		if (comment instanceof JavadocComment) {
			JavadocComment javadocComment = (JavadocComment) comment;
			String content = javadocComment.getContent();
			String commentLines[] = content.split("\n");
			if (commentLines == null || commentLines.length < 2) {
				log.warn("javadoc comment for classOrInterface {} is not found",classOrInterfaceName);
			}else{
				ClassInfo ci=new ClassInfo();
				List<JavaDocCommentElement> commentElements = JavaDocCommentParser.parse(Arrays.asList(commentLines));
				Optional<Description> optionalDescription=commentElements.stream()
				    .filter(commentElement->commentElement instanceof  Description)
				    .map(Description.class::cast)
				    .findFirst();
				optionalDescription.ifPresent(description->{
					String nodeFullName=getNodeFullName(classOrInterfaceDeclaration, context);
					ci.setName(classOrInterfaceName);
					ci.setComment(description.getContent());
					context.saveClassComment(nodeFullName, ci);
				});
				commentElements.stream()
				    .filter(BlockTag.class::isInstance)
				    .map(BlockTag.class::cast)
				    .forEach(bt->bt.onParseEnd(ci));
			}
		}else{
			log.warn("javadoc comment for classOrInterface {} is not found",classOrInterfaceName);
		}
		return true;
    }
    
    private static void parseMethodComment(MethodDeclaration method, FileVisitorContext context){
    	String fullMethodName=getNodeFullName(method, context);
    	String methodName = method.getName().asString();
		String className = ((ClassOrInterfaceDeclaration) method.getParentNode().get()).getName().asString();
		methodName = className + "#" + methodName;
		Comment comment = method.getComment().orElse(null);
		if (comment instanceof JavadocComment) {
			JavadocComment javadocComment = (JavadocComment) comment;
			String content = javadocComment.getContent();
			try {
				MethodInfo mi=doParseMethodJavadocComment(method, methodName, content); 
				context.saveMethodComment(fullMethodName,mi);
			}catch(Throwable e) {
				String msg="Parsing Method:"+fullMethodName+" Failed!";
				throw new SourceParseException(msg,e);
			}
		} else {
			log.warn("method {} has no javadoc comment",fullMethodName);
		}
    }
    
    private static MethodInfo doParseMethodJavadocComment(MethodDeclaration m, String methodName, String content) {
		MethodInfo mi = new MethodInfo();
		mi.setName(methodName);
		String commentLines[] = content.split("\n");
		List<JavaDocCommentElement> commentElements = JavaDocCommentParser.parse(Arrays.asList(commentLines));
		//set block tags
		commentElements.stream()
	        .filter(BlockTag.class::isInstance)
			.map(BlockTag.class::cast)
			.forEach(cb->cb.onParseEnd(mi));
		//set description
		commentElements.stream()
		    .filter(Description.class::isInstance)
		    .map(Description.class::cast)
		    .forEach(d->{
		    	mi.setComment(d.getContent());
		    });
		
		return mi;
	}
    
    private static boolean filterAndParseFieldComment(Node n, FileVisitorContext context) {
   	 
        if(!(n instanceof FieldDeclaration)) {
			return true;
		}
        String fullFieldName=getNodeFullName(n, context);
	    FieldDeclaration field = (FieldDeclaration) n;
	    String fieldName =extractFieldName(field);
	    if(fieldName==null) {
	    	return true;
	    }
	    String trimComment=extractFieldComment(field);
		if (trimComment.length() > 0) {
			context.saveFieldComment(fullFieldName, new FieldInfo(trimComment, fieldName, field));
		} else {
			log.warn("the java document comment for field:{} is empty",fullFieldName);
		}
        return true;
	}
    
    public static String getNodeFullName(Node node,FileVisitorContext context) {
    	String name=JavaParserUtils.getParentNodeNameList(node);
    	String packageName=context.getPackageName();
    	return StringUtils.isBlank(packageName)?name:packageName+"."+name;
    }
    
    private static String extractFieldName(FieldDeclaration field) {
		NodeList<VariableDeclarator> variables=field.getVariables();
		if(CollectionUtils.isEmpty(variables)) {
			log.error("the veriables for field:{} is empty",field);
			return null;
		}
		VariableDeclarator variable = field.getVariables().get(0);
        SimpleName variableSimpleName = variable.getName();
        return variableSimpleName.toString();
	}
	private static String extractFieldComment(FieldDeclaration field) {
		Comment comment = field.getComment().orElse(null);
		if (!(comment instanceof JavadocComment)) {
		    return "";
		}
		JavadocComment jdoc = (JavadocComment) comment;
		String content = jdoc.getContent();
		StringBuilder trimComment = new StringBuilder();
		String[] lines = content.split("\n");
		if (lines != null && lines.length > 0) {
		    for (int i = 0; i < lines.length; i++) {
				String line = lines[i].trim();
				line = line.replaceFirst("\\*", "");
				if (!StringUtils.isEmpty(line)) {
					trimComment.append(line);
				}
			}
		}
		return trimComment.toString();
	}
  
}
