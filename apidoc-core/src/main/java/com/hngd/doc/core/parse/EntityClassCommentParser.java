
package com.hngd.doc.core.parse;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.doc.core.FieldInfo;
import com.hngd.doc.core.util.ClassUtils;
import com.hngd.doc.core.util.JavaFileUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.Type;
/**
 * @deprecated replaced by {@link CommonClassCommentParser}
 * @author wanazi
 *
 */
@Deprecated
public class EntityClassCommentParser {
	private static Logger logger = LoggerFactory.getLogger(EntityClassCommentParser.class);
	public static Map<String, FieldInfo> fieldComments = new HashMap<String, FieldInfo>();

	public static void init(String[] roots) {
		for (String root : roots) {
			File file = new File(root);
			parseFile(file);
		}
	}
	public static void initRecursively(File root) {
		
		if(root.isDirectory()) {
			File[] files=root.listFiles();
			if(files==null) {
				return ;
			}
			for(File file:files) {
				if(file.isDirectory()) {
					initRecursively(file);
				}else if(file.isFile()) {
					parse(file);
				}
			}
		}
		
	}
    @Deprecated
	private static void parseDirectory(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File files[] = directory.listFiles();
			Arrays.asList(files).stream()
			    .filter(File::isFile)
				.filter(f -> f.getName().endsWith(".java") && !f.getName().endsWith("Example.java"))
				.forEach(f ->parse(f));
		}
	}
	private static void parseFile(File file) {
		if(file.exists() && file.isFile() && JavaFileUtils.isJavaFile(file)) {
			if(file.getName().endsWith("Example.java")) {
				logger.info("ignore java file:{}",file.getAbsolutePath());
				return ;
			}
			parse(file);
		}
	}
   
	private static void parse(File f) {
		logger.info("parsing " + f.getAbsolutePath());
		CompilationUnit cu = ClassUtils.parseClass(f);
		List<Node> nodes = cu.getChildNodes();
		nodes.stream().filter(n -> n instanceof ClassOrInterfaceDeclaration).flatMap(n -> {
			return n.getChildNodes().stream();
		}).filter(n -> n instanceof FieldDeclaration).forEach(n -> {
			FieldDeclaration f1 = (FieldDeclaration) n;
			VariableDeclarator variable = f1.getVariables().get(0);
			if (variable != null) {
				SimpleName variableId = variable.getName();
				if (variableId != null) {
					String variableName = variableId.toString();
					// Type type = f1.getType();
					Comment comment = n.getComment().orElse(null);
					if (comment instanceof JavadocComment) {
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
						ClassOrInterfaceDeclaration c = (ClassOrInterfaceDeclaration) f1.getParentNode().orElse(null);
						if (trimComment.length() > 0) {
							String key = c.getName() + "#" + variableName;
							fieldComments.put(key, new FieldInfo(trimComment.toString(), variableName, f1));
							if(!c.getName().toString().endsWith("Info")) {
							    key = c.getName() + "Info#" + variableName;
								fieldComments.put(key, new FieldInfo(trimComment.toString(), variableName, f1));
							}
						}
					} else {
						logger.error(f1 + " no jdoc" + comment);
					}
				} else {
					logger.error("variableId is null");
				}
			} else {
				logger.error("variable is null");
			}
		});
	}

	public static void main(String[] args) {
		System.out.println(fieldComments.size());
	}
}
