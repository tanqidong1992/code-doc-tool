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
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.shared.utils.io.FileUtils;
import org.codehaus.plexus.util.MatchPatterns;
import org.springframework.util.CollectionUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.hngd.exception.SourceParseException;

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
public class SourceParserContext {
	
	private CommentStore commentStore;
	private String excludes;
	private String includes;
	MatchPatterns includePatterns;
	MatchPatterns excludePatterns;
	public CommentStore getCommentStore() {
		return this.commentStore;
	}
	public SourceParserContext(String includes,String excludes) {
		this.excludes = excludes;
		this.includes = includes;
		if(StringUtils.isNotBlank(excludes)) {
			String []excludeArray=StringUtils.split(excludes, ",");
			excludePatterns=MatchPatterns.from(excludeArray);
		}
		if(StringUtils.isNotBlank(includes)) {
			String []includeArray=StringUtils.split(includes, ",");
			includePatterns=MatchPatterns.from(includeArray);
		}
		commentStore=new CommentStore();
	}
    
	public SourceParserContext() {
		this(null,null);
	}
	static {
		ExtensionManager.enableExtension(TimeBlock.class);
		ExtensionManager.enableExtension(MobileBlock.class);
		ExtensionManager.enableExtension(AuthorBlock.class);
	}

	 
    public  void initSource(File sourceBaseDirectory) {
    	boolean includeBasedir=true;
		try {
			List<File> files=FileUtils.getFiles(sourceBaseDirectory, includes, excludes, includeBasedir);
			files.stream()
			  .parallel()
	          .filter(JavaFileUtils::isJavaSourceFile)
			  .forEach(this::parse);
		} catch (IOException e) {
			throw new SourceParseException("读取源代码列表失败", e);
		}
	}
    public  void initSourceInJar(List<File> sourceJarFiles) {
    	 if(CollectionUtils.isEmpty(sourceJarFiles)) {
    		 return ;
    	 }
		 sourceJarFiles.stream()
		     .parallel()
		     .forEach(jarFile -> {
				try {
					doParseSourceJarFile(jarFile);
				} catch (IOException e) {
					String msg="Read file:"+jarFile.getName()+" failed!";
					log.warn(msg,e);
					//throw new SourceParseException(msg, e);
				}
			});
	}
    public void doParseSourceJarFile(File file) throws IOException{
    	JarFile jarFile= new JarFile(file);
		Enumeration<JarEntry> entries=jarFile.entries();
		List<JarEntry> filteredJarEntries=new ArrayList<>();
		while(entries.hasMoreElements()) {
			JarEntry entry=entries.nextElement();
			String name=entry.getName();
			if(isInclude(name)) {
				filteredJarEntries.add(entry);
			}
		}
		filteredJarEntries.stream()
		    .parallel()
		    .forEach(je->doParseJarEntry(jarFile,je));
    }
	private void doParseJarEntry(JarFile jarFile, JarEntry je) {
		try(InputStream in=jarFile.getInputStream(je)){
			CompilationUnit cu= ClassUtils.parseClass(in);
			doParseCompilationUnit(cu);
		}catch(IOException e) {
			String msg="Parse file:"+jarFile.getName()+"!"+je.getName()+" failed!";
			 throw new SourceParseException(msg, e);
		}
	}

	private boolean isInclude(String name) {
		
		if(includePatterns==null || includePatterns.matches(name, true)) {
			if(excludePatterns==null || !excludePatterns.matches(name, true)) {
				return true;
			}
		}
		return false;
	}
    private void doParseCompilationUnit(CompilationUnit cu) {
    	Optional<PackageDeclaration>  optionalPackageDeclaration=cu.getPackageDeclaration();
		String packageName="";
		if(optionalPackageDeclaration.isPresent()) {
			PackageDeclaration pd=optionalPackageDeclaration.get();
			packageName=pd.getNameAsString();
		}
		FileVisitorContext context=new FileVisitorContext(packageName);
		cu.accept(new MySourceVisitor(), context);
		
		FileParseResult result=context.getParseResult();
		commentStore.save(result);
    }
	public  void parse(File f) {
		 
		CompilationUnit cu=ClassUtils.parseClass(f);
		doParseCompilationUnit(cu);
	}
     
	
}
