package org.codegen;

import java.io.IOException;

public class JSCodeGeneratorTest {

	   public static void main(String[] args) throws IOException
	    {
	    	String serviceUrl="/";
	        String filePath="./src/main/java/org/codegen/HttpTestCodeGenerator.java";
	        filePath="E:\\Code\\STSCode\\NativeClient\\src\\main\\java\\com\\hngd\\web\\NativeController.java";
	        //filePath="E:\\Code\\STSCode\\hnvmns-base-data\\src\\main\\java\\com\\hngd\\web\\controller\\PersonnelController.java";
	    	filePath="E:\\Code\\spring-code\\hnvmns-event-processor\\src\\main\\java\\com\\hngd\\web\\controller\\LinkageController.java";
			//useBeetl();
	    	String str=JSCodeGenerator.generate(filePath,serviceUrl,"ajax");
	    	System.out.println(str);
	    }
}
