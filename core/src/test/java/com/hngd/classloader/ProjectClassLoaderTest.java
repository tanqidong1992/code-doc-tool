package com.hngd.classloader;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.RestController;

public class ProjectClassLoaderTest {

    @Test
    public void testJarLoad() throws ClassNotFoundException{
        
        String s="./test-data/core-test-2.4.0-SNAPSHOT.jar";
        ProjectClassLoader cl=
                new ProjectClassLoader(this.getClass().getClassLoader());
        cl.addClasspath(s);
        List<String> classes=cl.listAllClass();
        classes.forEach(System.out::println);
        Class<?> cls=cl.loadClass("com.hngd.web.controller.RoleController");
        Assert.assertTrue(cls!=null);
  
    }
    
    @Test
    public void testNestedJarLoad() throws ClassNotFoundException{
        
        String s="./test-data/hnvmns-java-sample-0.0.1.jar";
        ProjectClassLoader cl=
                new ProjectClassLoader(this.getClass().getClassLoader());
        cl.addClasspath(s);
        List<String> classes=cl.listAllClass();
        classes.forEach(System.out::println);
        Class<?> cls=cl.loadClass("org.apache.logging.log4j.core.net.Priority");
        Assert.assertTrue(cls!=null);
  
    }
    
    @Test
    public void loadFromDirectoryTest() throws ClassNotFoundException {
        
        String s="../core-test/target/classes";
        ProjectClassLoader cl=new ProjectClassLoader(getClass().getClassLoader());
        cl.addClasspath(s);
        cl.listAllClass().forEach(System.out::println); 
        Class<?> cls=cl.loadClass("com.hngd.web.controller.RoleController");
        Assert.assertTrue(cls!=null);
        RestController rest=cls.getAnnotation(RestController.class);
        Assert.assertTrue(rest!=null);

    }
}
