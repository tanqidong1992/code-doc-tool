package com.hngd.s2m;

import java.io.File;

public class OpenAPIToMarkdownTest {

	public static void main(String[] args) {
		
		File openAPIFile=new File("test-data/api.json");
		OpenAPIToMarkdown.openAPIToMarkdown(openAPIFile, null, new File("test-output"));
	}
}
