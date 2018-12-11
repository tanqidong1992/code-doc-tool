
package com.hngd.doc.core.parse;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.doc.core.MethodInfo;
import com.hngd.doc.core.MethodInfo.ParameterInfo;
import com.hngd.doc.core.parse.CommentElement.ParamElement;
import com.hngd.doc.core.parse.extension.AuthorElement;
import com.hngd.doc.core.parse.extension.ExtensionManager;
import com.hngd.doc.core.parse.extension.MobileElement;
import com.hngd.doc.core.parse.extension.TimeElement;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;

public class ControllerClassCommentParser {
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
		if (file.exists() && file.isDirectory()) {
			File files[] = file.listFiles();
			Arrays.asList(files).stream()
			.filter(f -> f.getName().endsWith(".java"))
			.forEach(f -> {
				try {
					parse(f);
				} catch (Exception e) {
					logger.error("", e);
				}
			});
		} else {
			logger.error("file[" + root + "] is not found or a directory");
		}
	}

	public static void parse(File f) throws ParseException, IOException {
		CompilationUnit cu = JavaParser.parse(f);
		List<Node> nodes = cu.getChildNodes();
		nodes.stream().filter(n -> n instanceof ClassOrInterfaceDeclaration).flatMap(n -> {
			ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) n;
			Comment comment = cid.getComment().get();
			String name = cid.getName().asString();
			if (comment instanceof JavadocComment) {
				JavadocComment jdc = (JavadocComment) comment;
				String content = jdc.getContent();
				String commentLines[] = content.split("\n");
				if (commentLines != null && commentLines.length > 2) {
					List<CommentElement> ces = ClassCommentParser.parseMethodComment(Arrays.asList(commentLines));
					for (CommentElement ce : ces) {
						if (ce instanceof CommentElement.DescElement) {
							classComments.put(name, ce.comment);
						}
					}
				} else {
				}
			}
			return n.getChildNodes().stream();
		}).filter(n -> {
			
			if(n instanceof MethodDeclaration){
				return true;
			}
			
			if (n instanceof MethodDeclaration) {
				MethodDeclaration m = (MethodDeclaration) n;
				List<AnnotationExpr> as = m.getAnnotations();
				if (as != null && as.size() > 0) {
					boolean foundRequestMapping = false;
					for (int i = 0; i < as.size(); i++) {
						AnnotationExpr a = as.get(i);
						if ("RequestMapping".equals(a.getName().toString())) {
							foundRequestMapping = true;
						}
					}
					return foundRequestMapping;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}).forEach(n -> {
			MethodDeclaration m = (MethodDeclaration) n;
			String methodName = m.getName().asString();
			String className = ((ClassOrInterfaceDeclaration) m.getParentNode().get()).getName().asString();
			methodName = className + "#" + methodName;
			Comment comment = m.getComment().orElse(null);
			if (comment instanceof JavadocComment) {
				JavadocComment jdoc = (JavadocComment) m.getComment().orElse(null);
				if (jdoc != null) {
					String content = jdoc.toString();
					parse(m, methodName, content);
				}
			} else {
				logger.error(className + "." + m.getName() + " has no javadoc comment");
			}
		});
	}

	private static void parse(MethodDeclaration m, String methodName, String content) {
		MethodInfo mi = new MethodInfo();
		mi.methodName = methodName;
		mi.parameters = new ArrayList<ParameterInfo>();
		String commentLines[] = content.split("\n");
		if (commentLines != null && commentLines.length > 2) {
			List<CommentElement> ces = ClassCommentParser.parseMethodComment(Arrays.asList(commentLines));
			for (CommentElement ce : ces) {
				if (ce instanceof CommentElement.DescElement) {
					mi.comment = ce.comment;
				} else if (ce instanceof CommentElement.ParamElement) {
					ParameterInfo pi = new ParameterInfo();
					ParamElement pe = (ParamElement) ce;
					pi.parameterName = pe.paramName;
					pi.comment = pe.comment;
					mi.parameters.add(pi);
				} else if (ce instanceof CommentElement.ReturnElement) {
					mi.retComment = ce.comment;
				} else if (ce instanceof TimeElement) {
					ParameterInfo pi = new ParameterInfo();
					TimeElement te = (TimeElement) ce;
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
