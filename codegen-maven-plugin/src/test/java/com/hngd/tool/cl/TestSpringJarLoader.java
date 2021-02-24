package com.hngd.tool.cl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.classloader.ProjectClassLoader;
import com.hngd.parser.source.SourceParserContext;
import com.hngd.parser.spring.ClassParser;

public class TestSpringJarLoader {

    public static void main(String[] args) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        
        String path="./test-data/tar_res-0.0.1.jar";
        Logger logger=LoggerFactory.getLogger("XX");
        ProjectClassLoader loader=new ProjectClassLoader(TestSpringJarLoader.class.getClassLoader());
        File file=new File(path);
        loader.addClasspath(file.getAbsolutePath());
        
        //Class<?> clazz1=loader.loadClass("org.apache.logging.log4j.core.Filter",true);
        List<String> classNames=loader.listAllClass();
        classNames.stream()
            .filter(name->name.contains(".Filter"))
            .forEach(System.out::println);            
        classNames
        .stream()
        .filter(clazz->clazz.contains("controller"))
        //.filter(clazz->!clazz.contains("$"))
        .forEach(name->{
            System.out.println(name);
            Class<?> clazz = null;
            
            try {
                clazz = loader.loadClass(name, false);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            SourceParserContext parserContext=new SourceParserContext();
            ClassParser cp=new ClassParser(parserContext.getCommentStore());
            
            cp.parseModule(clazz);
            RestController a=clazz.getAnnotation(RestController.class);
             
            /**
            for(Method method:clazz.getDeclaredMethods()) {
                System.out.println(clazz.getName()+"."+method.getName());
            }
            */
        });
         
        
        List<String> allClass=loader.listAllClass();
        allClass.stream()
        .filter(name->name.contains("hngd"))
        .forEach(System.out::println);
        System.out.println("a");

    }

}
