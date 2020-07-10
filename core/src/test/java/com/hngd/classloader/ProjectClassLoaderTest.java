package com.hngd.classloader;

import java.util.List;

public class ProjectClassLoaderTest {

	public static void main(String[] args) throws ClassNotFoundException {
		
		String s="W:\\workspaces\\build-tools\\hnvmns-java-sample\\target\\hnvmns-java-sample-0.0.1.jar";
		ProjectClassLoader cl=new ProjectClassLoader();
		cl.addClasspath(s);
		cl.listAllClass().forEach(System.out::println);
		
		List<String> classes=cl.listAllClass();
		String className="com.hngd.common.exception.HNException";
		if(classes.contains(className)) {
			System.out.println("a");
		}
		cl.loadClass(className);
		//System.exit(0);
		classes.stream()
		    //.filter(cn->cn.startsWith("com.hngd"))
		    .forEach(cn->{
			try {
				cl.loadClass(cn);
			} catch (ClassNotFoundException e) {
				String s1=e.getMessage();
				System.out.println(s1);
			}
		});
		
		
	}
}
