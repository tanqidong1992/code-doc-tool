package com.hngd.classloader;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.RestController;

public class ProjectClassLoaderFromDirectoryTest {

    @Test
    public void main() throws ClassNotFoundException {
        
        String s="../core-test/target/classes";
        ProjectClassLoader cl=new ProjectClassLoader(ProjectClassLoaderFromDirectoryTest.class.getClassLoader());
        cl.addClasspath(s);
        cl.listAllClass().forEach(System.out::println); 
        Class<?> cls=cl.loadClass("com.hngd.web.controller.RoleController");
        Assert.assertTrue(cls!=null);
        RestController rest=cls.getAnnotation(RestController.class);
        Assert.assertTrue(rest!=null);

    }
}
