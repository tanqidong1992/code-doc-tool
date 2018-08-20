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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.doc.core.MethodInfo;
import com.hngd.doc.core.MethodInfo.ParameterInfo;
import com.hngd.doc.core.parse.CommentElement.DescElement;
import com.hngd.doc.core.parse.CommentElement.ParamElement;
import com.hngd.doc.core.parse.CommentElement.ReturnElement;
import com.hngd.doc.core.parse.extension.AuthorElement;
import com.hngd.doc.core.parse.extension.ExtensionManager;
import com.hngd.doc.core.parse.extension.MobileElement;
import com.hngd.doc.core.parse.extension.TimeElement;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.comments.Comment;
import japa.parser.ast.comments.JavadocComment;
import japa.parser.ast.expr.AnnotationExpr;

/**
 * @author tqd
 */
public class CommonClassCommentParser {
	static {
		ExtensionManager.enableExtension(TimeElement.class);
		ExtensionManager.enableExtension(MobileElement.class);
		ExtensionManager.enableExtension(AuthorElement.class);
	}
	private static Logger logger = LoggerFactory.getLogger(ControllerClassCommentParser.class);
	public static Map<String, String> classComments = new HashMap<>();
	public static Map<String, MethodInfo> methodComments = new HashMap<>();

	public static void init(String root) {
		File file = new File(root);
		if(!file.exists()){
			logger.error("file {} is not found",file.getAbsolutePath());
		    return;
		}
		if(!file.isDirectory()){
			logger.error("file {} is not a directory",file.getAbsolutePath());
			return ;
		}
        File files[] = file.listFiles();
		Arrays.asList(files).stream()
	        .filter(CommonClassCommentParser::isJavaSourceFile)
			.forEach(CommonClassCommentParser::parse);
	}
    public static boolean isJavaSourceFile(File file){
    	return file.getName().endsWith(".java");
    }
    
    public static boolean filterAndParseClassComment(Node node){
    	if(node instanceof ClassOrInterfaceDeclaration){
    		
			ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
			Comment comment = classOrInterfaceDeclaration.getComment();
			String classOrInterfaceName = classOrInterfaceDeclaration.getName();
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
				logger.warn("javadoc comment for classOrInterface {}",classOrInterfaceName);
			}
    		return true;
    	}
    	return false;
    }
	public static void parse(File f) {
		CompilationUnit cu=null;
		try(InputStream in=new FileInputStream(f)) {
			Reader reader=new BufferedReader(new InputStreamReader(in, "UTF-8"));
			cu = JavaParser.parse(reader, true);
		} catch (ParseException | IOException e) {
			logger.error("", e);
		}
		if(cu==null){
			return ;
		}
		List<Node> nodes = cu.getChildrenNodes();
		nodes.stream()
		.filter(CommonClassCommentParser::filterAndParseClassComment)
		.flatMap(n -> n.getChildrenNodes().stream())
		.filter(n -> n instanceof MethodDeclaration)
		.map(MethodDeclaration.class::cast)
		.forEach(CommonClassCommentParser::parseMethodComment);
	}
    private static void parseMethodComment(MethodDeclaration method){
    	String methodName = method.getName();
		String className = ((ClassOrInterfaceDeclaration) method.getParentNode()).getName();
		methodName = className + "#" + methodName;
		Comment comment = method.getComment();
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
}
