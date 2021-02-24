package com.hngd.classloader;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.RestController;

public class ProjectClassLoaderFromDirectoryTest {

    public static void main(String[] args) throws ClassNotFoundException {
        
        String s="W:\\workspaces\\build-tools\\hnvmns-java-sample\\target\\classes";
        ProjectClassLoader cl=new ProjectClassLoader(ProjectClassLoaderFromDirectoryTest.class.getClassLoader());
        cl.addClasspath(s);
        //cl.listAllClass().forEach(System.out::println);
        
        //cl.loadClass("com.xxl.rpc.remoting.net.impl.jetty.client.JettyClient$1");
        
        List<String> classes=cl.listAllClass();
        
        Class<?> cls=cl.loadClass("com.hngd.web.controller.RoleController");
        Annotation[] as=cls.getAnnotations();
        RestController rest=cls.getAnnotation(RestController.class);
        System.out.println(rest);
 
        cl.listAllClass().forEach(System.out::println); 
        
    }
}
