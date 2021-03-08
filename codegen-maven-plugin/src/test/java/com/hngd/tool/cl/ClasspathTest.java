package com.hngd.tool.cl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;


import com.hngd.classloader.ProjectClassLoader;

public class ClasspathTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        ProjectClassLoader loader=new ProjectClassLoader(ClasspathTest.class.getClassLoader());
        List<String> classpaths=Files.readAllLines(new File("./test-data/classpaths.txt").toPath(),
                StandardCharsets.UTF_8);
        loader.addClasspath(classpaths.toArray(new String[0]));
        String testClassName="com.hngd.alarm.center.config.AppProperties";
        Class<?> clazz=loader.loadClass(testClassName);
        System.out.println(clazz.getName());
        
        Thread thread=new Thread(()->{
            Class<?> c1=null;
            try {
                c1 = Class.forName(testClassName);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(c1.getName());
        });
        thread.setContextClassLoader(loader);
        thread.start();
        
    
    }
}
