package com.hngd.classloader;

import java.util.List;

public class ProjectClassLoaderTest {

	public static void main(String[] args) throws ClassNotFoundException {
		
		String s="W:\\workspaces\\build-tools\\hnvmns-java-sample\\target\\hnvmns-java-sample-0.0.1.jar";
		ProjectClassLoader cl=new ProjectClassLoader();
		cl.addClasspath(s);
		//cl.listAllClass().forEach(System.out::println);
		
		//cl.loadClass("com.xxl.rpc.remoting.net.impl.jetty.client.JettyClient$1");
		
		List<String> classes=cl.listAllClass();
 
		classes.stream()
		    //.filter(cn->cn.startsWith("com.hngd"))
		    .forEach(cn->{
			try {
				cl.loadClass(cn);
				System.out.println(cn+"-->");
			} catch (ClassNotFoundException e) {
				
			}catch(UnsupportedClassVersionError e) {
				String s1=e.getMessage();
				e.printStackTrace();
				System.exit(0);
			}
		});
		
		
	}
}
