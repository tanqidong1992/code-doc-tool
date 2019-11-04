package com.hngd.doc.core;

import java.io.File;

import org.junit.Test;

import com.hngd.parser.source.CommonClassCommentParser;
import com.hngd.parser.source.SourceParser;

public class TestLamdbaParse {

	@Test
	public void testLamdba(){
		String file="E:\\Code\\spring-code\\hnvmns-event-processor\\src\\main\\java\\com\\hngd\\web\\controller\\LinkageController.java";
		CommonClassCommentParser.parse(new File(file));
		SourceParser.parse(new File(file));
		
	}
}
