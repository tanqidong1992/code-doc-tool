package com.hngd.doc.core;

import java.io.File;

import org.junit.Test;

import com.hngd.doc.core.parse.CommonClassCommentParser;
import com.hngd.doc.core.parse.ModuleParser;

public class TestLamdbaParse {

	@Test
	public void testLamdba(){
		String file="E:\\Code\\spring-code\\hnvmns-event-processor\\src\\main\\java\\com\\hngd\\web\\controller\\LinkageController.java";
		CommonClassCommentParser.parse(new File(file));
		ModuleParser.parse(new File(file));
		
	}
}
