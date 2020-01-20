package com.hngd.parser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.hngd.test.dto.Teacher;
import com.hngd.doc.core.ClassCommentParseTest;
import com.hngd.openapi.OpenAPITool;
import com.hngd.parser.javadoc.JavaDocCommentElement;
import com.hngd.parser.javadoc.JavaDocCommentParser;
import com.hngd.parser.source.ParserContext;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class SummatyCommentParser {

	public static void main(String[] args) throws URISyntaxException, IOException {
		URL url = SummatyCommentParser.class.getResource("summary-comment.txt");
	    Path path = Paths.get(url.toURI());
	    List<String> commentLines = Files.readAllLines(path);
	    	
		ParserContext pc=new ParserContext();
		List<JavaDocCommentElement> jdc=JavaDocCommentParser.parse(commentLines);
		pc.printResult();

	}

}
