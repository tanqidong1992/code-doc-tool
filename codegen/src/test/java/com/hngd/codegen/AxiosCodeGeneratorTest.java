package com.hngd.codegen;

import java.io.IOException;

import com.hngd.codegen.JSCodeGenerator;

public class JSCodeGeneratorTest {

	   public static void main(String[] args) throws IOException
	    {
	    	String serviceUrl="/";
	        String filePath="./src/main/java/org/codegen/HttpTestCodeGenerator.java";
	        filePath="E:\\Code\\STSCode\\NativeClient\\src\\main\\java\\com\\hngd\\web\\NativeController.java";
	        //filePath="E:\\Code\\STSCode\\hnvmns-base-data\\src\\main\\java\\com\\hngd\\web\\controller\\PersonnelController.java";
	    	filePath="E:\\workspaces\\hnvmns-code-modules\\inspection-system\\src\\main\\java\\com\\hngd\\web\\controller\\DepartmentController.java";
			filePath="D:\\company\\projects\\inspection-system\\inspection-system\\src\\main\\java\\com\\hngd\\web\\controller\\DepartmentController.java";
			filePath="D:\\company\\projects\\inspection-system\\education-training-system\\src\\main\\java\\com\\hngd\\web\\controller\\TrainingRecordController.java";
			filePath="../..//hnvmns-java-sample/src/main/java/com/hngd/web/controller/SampleController.java";
			//useBeetl();
	    	String str=JSCodeGenerator.generate(filePath,serviceUrl,"ajax");
	    	System.out.println(str);
	    	str=JSCodeGenerator.generate(filePath,serviceUrl,"axios");
	    	System.out.println(str);
	    }
}
