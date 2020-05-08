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
package com.hngd.parser.source;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.shared.utils.io.FileUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.hngd.exception.SourceParseException;
import com.hngd.parser.entity.ClassInfo;
import com.hngd.parser.entity.FieldInfo;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.entity.ParameterInfo;
import com.hngd.parser.javadoc.BlockTag.AuthorBlock;
import com.hngd.parser.javadoc.extension.ExtensionManager;
import com.hngd.parser.javadoc.extension.MobileBlock;
import com.hngd.parser.javadoc.extension.TimeBlock;
import com.hngd.utils.ClassUtils;
import com.hngd.utils.JavaFileUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tqd
 */
@Slf4j
public class ParserContext {
	
	private String excludes;
	private String includes;
	
	public ParserContext(String includes,String excludes) {
		this.excludes = excludes;
		this.includes = includes;
	}

	public ParserContext() {
		
	}
	static {
		ExtensionManager.enableExtension(TimeBlock.class);
		ExtensionManager.enableExtension(MobileBlock.class);
		ExtensionManager.enableExtension(AuthorBlock.class);
	}
	private  Map<String, ClassInfo> classComments = new ConcurrentHashMap<>();
	private  Map<String, MethodInfo> methodComments = new ConcurrentHashMap<>();
	private  Map<String, FieldInfo> fieldComments = new ConcurrentHashMap<>();
	 
    public  void initRecursively(File directory) {
    	boolean includeBasedir=true;
		try {
			List<File> files=FileUtils.getFiles(directory, includes, excludes, includeBasedir);
			files.stream()
			  .parallel()
	          .filter(JavaFileUtils::isJavaSourceFile)
			  .forEach(this::parse);
		} catch (IOException e) {
			throw new SourceParseException("读取源代码列表失败", e);
		}
	}
	 
	public  void parse(File f) {
		 
		CompilationUnit cu=ClassUtils.parseClass(f);
		Optional<PackageDeclaration>  optionalPackageDeclaration=cu.getPackageDeclaration();
		String packageName="";
		if(optionalPackageDeclaration.isPresent()) {
			PackageDeclaration pd=optionalPackageDeclaration.get();
			packageName=pd.getNameAsString();
		}
		FileVisitorContext context=new FileVisitorContext(packageName);
		cu.accept(new MySourceVisitor(), context);
		
		FileParseResult result=context.getParseResult();
	    result.getClassComments().forEach((k,v)->{
	    	this.classComments.put(k, v);
	    });
	    result.getFieldComments().forEach((k,v)->{
	    	this.fieldComments.put(k, v);
	    });
	    result.getMethodComments().forEach((k,v)->{
	    	this.methodComments.put(k, v);
	    });
	}
     
	public  String getMethodComment(String className, String methodName) {
		String key = className + "." + methodName;
		MethodInfo mi = methodComments.get(key);
		return mi != null ? mi.getComment() : null;
	}
	public  Optional<MethodInfo> getMethodInfo(Executable method) {
		String key = methodKey(method);
		MethodInfo mi = methodComments.get(key);
		return Optional.ofNullable(mi);
	}
	
	public  String getMethodComment(Executable method) {
		String key = methodKey(method);
		MethodInfo mi = methodComments.get(key);
		return mi != null ? mi.getComment() : null;
	}
	public  String getFieldComment(Field field) {
		String key = fieldKey(field);
		FieldInfo mi = fieldComments.get(key);
		return mi != null ? mi.getComment() : null;
	}
	public  String getFieldComment(String className, String fieldName) {
		String key = className + "." + fieldName;
		FieldInfo mi = fieldComments.get(key);
		return mi != null ? mi.getComment() : null;
	}
	
	public  String getParameterComment(String className, String methodName, String parameterName) {
		String key = className + "." + methodName;
		MethodInfo mi = methodComments.get(key);
		String comment = null;
		if (mi != null) {
			Optional<ParameterInfo> op = mi.getParameters().stream()
					.filter(p -> p.getName().equals(parameterName))
					.findFirst();
			if (op.isPresent()) {
				comment = op.get().getComment();
			}
		}
		return comment;
	}

	public  String getParameterComment(String className, String methodName, int order) {
		String key = className + "." + methodName;
		MethodInfo mi = methodComments.get(key);
		String comment = null;
		if (mi != null) {
			if (order >= 0 && order < mi.getParameters().size()) {
				comment = mi.getParameters().get(order).getComment();
			}
		}
		return comment;
	}
	
	public  void printResult() {
		classComments.entrySet()
		.forEach(e->{
			System.out.println(e.getKey()+"-->"+e.getValue());
		});
		fieldComments.entrySet()
		.forEach(e->{
			System.out.println(e.getKey()+"-->"+e.getValue().getComment());
		});
		methodComments.entrySet()
		.forEach(e->{
			System.out.println(e.getKey()+"-->"+e.getValue().getComment());
			e.getValue().getParameters().forEach(p->{
				System.out.println("    "+p.getName()+"-->"+p.getComment());
			});
		});
		
	}
	
	public static  String classKey(Class<?> clazz) {
		String s="";
		Class<?> enclosingClass=clazz.getEnclosingClass();
		while(enclosingClass!=null) {
			String enclosingClassName=enclosingClass.getSimpleName();
			s=enclosingClassName+"."+s;
			enclosingClass=enclosingClass.getEnclosingClass();
		}
		if(StringUtils.isNotEmpty(s)) {
			String packageName=clazz.getPackage().getName();
			return packageName+"."+s+clazz.getSimpleName();
		}else {
			return clazz.getName();
		}
		
	}
	public  static String methodKey(Executable method) {
		Class<?> clazz=method.getDeclaringClass();
		return classKey(clazz)+"."+method.getName();
	}
	public  static String fieldKey(Field field) {
		Class<?> clazz=field.getDeclaringClass();
		return classKey(clazz)+"."+field.getName();
	}
	public  static String parameterKey(Parameter parameter) {
		Executable method=parameter.getDeclaringExecutable();
		return methodKey(method)+"."+parameter.getName();
	}

	public ClassInfo getClassComment(Class<?> cls) {
		String classKey=classKey(cls);
		return classComments.get(classKey);
	}
}
