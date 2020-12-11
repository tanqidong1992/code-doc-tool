package com.hngd.s2m;

import org.junit.Assert;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class OpenAPIToMarkdownTest {

	public static void main(String[] args) throws IOException {
		
		File openAPIFile=new File("openapi-doc/test-data/api.json");
		openAPIFile=new File(openAPIFile.getAbsolutePath());
		File markdownFile=OpenAPIToMarkdown.openAPIToMarkdown(openAPIFile, null, new File("test-output"));
		Assert.assertTrue(markdownFile.exists());
		//Desktop.getDesktop().open(markdownFile);
	}
}
