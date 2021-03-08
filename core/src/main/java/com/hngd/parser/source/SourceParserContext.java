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
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
    private SourceFileFilter sourceFileFilter;
    public CommentStore getCommentStore() {
        return this.commentStore;
    }
    public SourceParserContext(String includes,String excludes) {
        sourceFileFilter=new SourceFileFilter(includes, excludes);
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
        Collection<File> files=sourceFileFilter.filterFiles(sourceBaseDirectory);
        files.parallelStream()
            .filter(JavaFileUtils::isJavaSourceFile)
            .forEach(this::parse);
    }
    public  void initSourceInJar(List<File> sourceJarFiles) {
         if(CollectionUtils.isEmpty(sourceJarFiles)) {
             return ;
         }
         sourceJarFiles.parallelStream()
             .forEach(this::doParseSourceJarFile);
    }
    public void doParseSourceJarFile(File file) {
        JarFile jarFile=null;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            log.warn("Read jar file:"+file.getAbsolutePath()+" failed!",e);
            return ;
        }
        Enumeration<JarEntry> entries=jarFile.entries();
        List<JarEntry> filteredJarEntries=new ArrayList<>();
        while(entries.hasMoreElements()) {
            JarEntry entry=entries.nextElement();
            String name=entry.getName();
            if(sourceFileFilter.isInclude(name)) {
                filteredJarEntries.add(entry);
            }
        }
        JarFile immutableJarFile=jarFile;
        filteredJarEntries.parallelStream()
            .filter(entry->entry.getName().endsWith(".java"))
            .forEach(entry->doParseJarEntry(immutableJarFile,entry));
    }
    private void doParseJarEntry(JarFile jarFile, JarEntry je) {
        try(InputStream in=jarFile.getInputStream(je)){
            CompilationUnit cu= ClassUtils.parseClass(in);
            doParseCompilationUnit(cu);
        }catch(Exception e) {
            String msg="Parse file:"+jarFile.getName()+"!"+je.getName()+" failed!";
            throw new SourceParseException(msg, e);
        }
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
