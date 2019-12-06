package com.hngd.doc.core;

import java.io.File;

import org.junit.Test;

import com.hngd.parser.source.CommonClassCommentParser;

public class ClassCommentParser {

	@Test
	public void testParse(){
		
		File f=new File("E:\\Code\\STSCode\\hnvmns-auth\\src\\main\\java\\com\\hngd\\web\\controller\\UserController.java");
		CommonClassCommentParser.parse(f);
		CommonClassCommentParser.classComments.forEach((k,v)->{
			System.out.println(k+":"+v);
		});
		CommonClassCommentParser.methodComments.forEach((k,v)->{
			System.out.println(k+":"+v.getComment());
		});
	}
}
