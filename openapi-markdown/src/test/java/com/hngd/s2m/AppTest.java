package com.hngd.s2m;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.hngd.s2m.utils.OpenAPIUtils;

import io.swagger.v3.oas.models.OpenAPI;
 

/**
 * Unit test for simple App.
 */
public class AppTest {
	
   @Test
   public void test() {
	   File file=new File("./test-data/api.json");
       OpenAPI openAPI=OpenAPIUtils.loadFromFile(file);
      
       File outputDirectory=new File("test-output");
       OpenAPIToMarkdown.openAPIToMarkdown(openAPI,Arrays.asList("设备点检"), outputDirectory);
   }
}
