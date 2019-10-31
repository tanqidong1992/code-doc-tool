package com.hngd.base;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.jar.JarFile;

import com.api.doc.utils.ClassScanner;

public class ClassScannerTest {

	  public static void main(String[] args) throws URISyntaxException, IOException
	    {
	        URL url = ClassScanner.class.getResource("/");
	        Path path = Paths.get(url.toURI());
	        Properties properties = System.getProperties();
	        /*
	         * properties.forEach((k,v)->{ System.out.println(k+"-->"+v); });
	         */
	        String classpathDir = properties.getProperty("java.class.path");
	        String[] classpaths = classpathDir.split(";");
	        for (int i = 0; i < classpaths.length; i++)
	        {
	            String classpath = classpaths[i];
	            File file = new File(classpath);
	            if (file.isDirectory())
	            {
	                String rootclasspath = file.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\") + "\\\\";
	                ClassScanner.showChildren(rootclasspath, file);
	            } else if (file.getName().endsWith(".jar"))
	            {
	                JarFile jfile = new JarFile(file);
	                ClassScanner.showJarFile(jfile);
	            }
	        }
	    }

}
