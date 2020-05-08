package com.hngd.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.shared.utils.io.FileUtils;

public class FileFilterTest {

	public static void main(String[] args) throws IOException {
		
		 
        File directory=new File("W:\\company\\hnvmns9000\\business-modules\\hnvmns-site");
		String includes=null;//"**/*.java";
		boolean includeBasedir=true;
		String excludes=null;//"**/*Example.java,**/com/hngd/dao/*.*";
		List<File>  files=FileUtils.getFiles(directory, includes, excludes, includeBasedir);
		files.forEach(f->{
			System.out.println(f.getAbsolutePath());
			
		});
	}

}
