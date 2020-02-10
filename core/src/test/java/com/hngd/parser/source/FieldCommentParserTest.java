package com.hngd.parser.source;

import java.io.File;

public class FieldCommentParserTest {

	public static void main(String[] args) {
		ParserContext pc=new ParserContext();
		String path="../core-test/src/main/java/com/hngd/test/dto/MenuTreeNode.java";
		File f=new File(path);
		pc.parse(f);
		pc.printResult();

	}

}
