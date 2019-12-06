package com.hngd.doc.core;

import java.io.File;
import java.nio.file.Path;

import com.hngd.parser.source.CommonClassCommentParser;

public class CommonSourceFileParserTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file=new File("test-data/method-cooment.txt");
		CommonClassCommentParser.parse(file);
	}

}
