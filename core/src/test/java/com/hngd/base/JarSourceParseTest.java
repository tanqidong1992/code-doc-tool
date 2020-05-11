package com.hngd.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.shared.utils.io.DirectoryScanner;
import org.apache.maven.shared.utils.io.FileUtils;
import org.codehaus.plexus.util.MatchPatterns;

import com.github.javaparser.ast.CompilationUnit;
import com.hngd.parser.source.SourceParserContext;
import com.hngd.utils.ClassUtils;

public class JarSourceParseTest {

	public static void main(String[] args) throws IOException {
		
		String filePath="D:\\app\\maven-repo\\com\\hngd\\common\\common-web\\0.0.4-SNAPSHOT\\common-web-0.0.4-20191011.125520-11-sources.jar";
		File file=new File(filePath);
		SourceParserContext pc=new SourceParserContext("com/hngd/**/*.java", null);
		String path1="D:\\app\\maven-repo\\org\\postgresql\\postgresql\\42.2.2\\postgresql-42.2.2-sources.jar";
	    pc.initSourceInJar(Arrays.asList(new File(path1),file));
	 
		
		
		 
	}

}
