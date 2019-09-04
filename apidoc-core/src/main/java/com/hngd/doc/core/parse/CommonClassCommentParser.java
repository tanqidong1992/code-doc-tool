/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：CommonClassCommentParser.java
 * @时间：2017年5月6日 上午9:18:31
 * @作者：tqd
 * @备注：
 * @版本:
 */
package com.hngd.doc.core.parse;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.SimpleName;
import com.hngd.doc.core.FieldInfo;
import com.hngd.doc.core.MethodInfo;
import com.hngd.doc.core.ParameterInfo;
import com.hngd.doc.core.parse.CommentElement.DescElement;
import com.hngd.doc.core.parse.CommentElement.ParamElement;
import com.hngd.doc.core.parse.CommentElement.ReturnElement;
import com.hngd.doc.core.parse.extension.AuthorElement;
import com.hngd.doc.core.parse.extension.ExtensionManager;
import com.hngd.doc.core.parse.extension.MobileElement;
import com.hngd.doc.core.parse.extension.TimeElement;
import com.hngd.doc.core.util.ClassUtils;

/**
 * @author tqd
 */
public class CommonClassCommentParser {
	static {
		ExtensionManager.enableExtension(TimeElement.class);
		ExtensionManager.enableExtension(MobileElement.class);
		ExtensionManager.enableExtension(AuthorElement.class);
	}
	private static Logger logger = LoggerFactory.getLogger(CommonClassCommentParser.class);
	public static Map<String, String> classComments = new ConcurrentHashMap<>();
	public static Map<String, MethodInfo> methodComments = new ConcurrentHashMap<>();
	public static Map<String, FieldInfo> fieldComments = new ConcurrentHashMap<>();
	public static void parseFiles(String directoryPath) {
		File file = new File(directoryPath);
		if(!file.exists()){
			logger.error("file {} is not found",file.getAbsolutePath());
		    return;
		}
		if(!file.isDirectory()){
			logger.error("file {} is not a directory",file.getAbsolutePath());
			return ;
		}
        File files[] = file.listFiles();
        if(files==null) {
        	logger.error("direcotry {} is empty",file.getAbsolutePath());
        	return;
        }
		Arrays.asList(files).stream()
		    .parallel()
	        .filter(CommonClassCommentParser::isJavaSourceFile)
			.forEach(CommonClassCommentParser::parse);
	}
	
    public static void initRecursively(File directory) {
    	if(directory.isFile()) {
    		logger.warn("the file:{} is not a directory",directory.getAbsolutePath());
    		return;
    	}
		parseFiles(directory.getAbsolutePath());
		File[] files=directory.listFiles();
		if(files==null) {
			return ;
		}
		for(File file:files) {
			if(!file.isDirectory()) {
				continue;
			}
			parseFiles(file.getAbsolutePath());
			initRecursively(file);
		} 
	}
	
    public static boolean isJavaSourceFile(File file){
    	return file.getName().endsWith(".java");
    }
    
    public static boolean filterAndParseClassComment(Node node){
    	if(node instanceof ClassOrInterfaceDeclaration){
    		
			ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
			Comment comment = classOrInterfaceDeclaration.getComment().orElse(null);
			String classOrInterfaceName = classOrInterfaceDeclaration.getName().asString();
			if (comment instanceof JavadocComment) {
				JavadocComment javadocComment = (JavadocComment) comment;
				String content = javadocComment.getContent();
				String commentLines[] = content.split("\n");
				if (commentLines == null || commentLines.length < 2) {
					logger.warn("javadoc comment for classOrInterface {} is not found",classOrInterfaceName);
				}else{
					List<CommentElement> commentElements = ClassCommentParser.parseMethodComment(Arrays.asList(commentLines));
					Optional<DescElement> optionalDescComment=commentElements.stream()
					    .filter(commentElement->commentElement instanceof  DescElement)
					    .map(CommentElement.DescElement.class::cast)
					    .findFirst();
					optionalDescComment.ifPresent(descComment->{
						classComments.put(classOrInterfaceName, descComment.comment);
					});
					 
				}
			}else{
				logger.warn("javadoc comment for classOrInterface {} is not found",classOrInterfaceName);
			}
    		return true;
    	}
    	return false;
    }
	public static void parse(File f) {
		CompilationUnit cu=ClassUtils.parseClass(f);
		List<Node> nodes = cu.getChildNodes();
		nodes.stream()
		.parallel()
		.filter(CommonClassCommentParser::filterAndParseClassComment)
		.flatMap(n -> n.getChildNodes().stream().parallel())
		.filter(n->filterAndParseFieldComment(n))
		.filter(n -> n instanceof MethodDeclaration)
		.map(MethodDeclaration.class::cast)
		.forEach(CommonClassCommentParser::parseMethodComment);
	}
    private static void parseMethodComment(MethodDeclaration method){
    	String methodName = method.getName().asString();
		String className = ((ClassOrInterfaceDeclaration) method.getParentNode().get()).getName().asString();
		methodName = className + "#" + methodName;
		Comment comment = method.getComment().orElse(null);
		if (comment instanceof JavadocComment) {
			JavadocComment javadocComment = (JavadocComment) comment;
			String content = javadocComment.getContent();
			parse(method, methodName, content); 
		} else {
			logger.warn("method {} has no javadoc comment",className + "." + method.getName() );
		}
    }
	private static void parse(MethodDeclaration m, String methodName, String content) {
		MethodInfo mi = new MethodInfo();
		mi.methodName = methodName;
		mi.parameters = new ArrayList<ParameterInfo>();
		String commentLines[] = content.split("\n");
		if (commentLines != null && commentLines.length > 2) {
			List<CommentElement> commentElements = ClassCommentParser.parseMethodComment(Arrays.asList(commentLines));
		 
			
			for (CommentElement commentElement : commentElements) {
				if (commentElement instanceof DescElement) {
					mi.comment = commentElement.comment;
				} else if (commentElement instanceof ParamElement) {
					ParameterInfo parameterInfo = new ParameterInfo();
					ParamElement paramElement = (ParamElement) commentElement;
					parameterInfo.parameterName = paramElement.paramName;
					parameterInfo.comment = paramElement.comment;
					mi.parameters.add(parameterInfo);
				} else if (commentElement instanceof ReturnElement) {
					mi.retComment = commentElement.comment;
				} else if (commentElement instanceof TimeElement) {
				 
					TimeElement te = (TimeElement) commentElement;
					mi.createTime = te.createTime;
					mi.createTimeStr = te.createTimeStr;
				}
			}
		} else {
		}
		methodComments.put(methodName, mi);
	}

	public static String getMethodComment(String className, String methodName) {
		String key = className + "#" + methodName;
		MethodInfo mi = methodComments.get(key);
		return mi != null ? mi.comment : null;
	}

	public static String getParameterComment(String className, String methodName, String parameterName) {
		String key = className + "#" + methodName;
		MethodInfo mi = methodComments.get(key);
		String comment = null;
		if (mi != null) {

			Optional<ParameterInfo> op = mi.parameters.stream().filter(p -> p.parameterName.equals(parameterName))
					.findFirst();
			if (op.isPresent()) {
				comment = op.get().comment;
			}
		}
		return comment;
	}

	public static String getParameterComment(String className, String methodName, int order) {
		String key = className + "#" + methodName;
		MethodInfo mi = methodComments.get(key);
		String comment = null;
		if (mi != null) {

			if (order >= 0 && order < mi.parameters.size()) {
				comment = mi.parameters.get(order).comment;
			}
		}
		return comment;
	}
	private static String extractFieldName(FieldDeclaration field) {
		NodeList<VariableDeclarator> variables=field.getVariables();
		if(CollectionUtils.isEmpty(variables)) {
			logger.error("the veriables for field:{} is empty",field);
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
	private static boolean filterAndParseFieldComment(Node n) {
	 
        if(!(n instanceof FieldDeclaration)) {
			return true;
		}
	    FieldDeclaration field = (FieldDeclaration) n;
	    String variableName =extractFieldName(field);
	    if(variableName==null) {
	    	return true;
	    }
        // Type type = f1.getType();
	    String trimComment=extractFieldComment(field);
		ClassOrInterfaceDeclaration ownerClass = (ClassOrInterfaceDeclaration) field.getParentNode().orElse(null);
		if (trimComment.length() > 0) {
		    String key = ownerClass.getName() + "#" + variableName;
			fieldComments.put(key, new FieldInfo(trimComment.toString(), variableName, field));
			if(!ownerClass.getName().toString().endsWith("Info")) {
			    key = ownerClass.getName() + "Info#" + variableName;
				 fieldComments.put(key, new FieldInfo(trimComment.toString(), variableName, field));
			}
			
		} else {
			logger.warn("the java document comment for field:{} is empty",ownerClass.getName() + "#" + variableName);
		}
        return true;
	}

	public static void printResult() {
		classComments.entrySet()
		.forEach(e->{
			System.out.println(e.getKey()+"-->"+e.getValue());
		});
		fieldComments.entrySet()
		.forEach(e->{
			System.out.println(e.getKey()+"-->"+e.getValue().comment);
		});
		methodComments.entrySet()
		.forEach(e->{
			System.out.println(e.getKey()+"-->"+e.getValue().comment);
		});
		
	}
}
