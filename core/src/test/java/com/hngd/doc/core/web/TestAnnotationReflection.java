package com.hngd.doc.core.web;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/haha")
public class TestAnnotationReflection {

    @Test
    public void testGetHHH() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        
        RequestMapping rm=TestAnnotationReflection.class.getAnnotation(RequestMapping.class);
        Method[] methods=RequestMapping.class.getDeclaredMethods();
        for(int i=0;i<methods.length;i++){
            System.out.println(methods[i]);
        }
        Method m=RequestMapping.class.getDeclaredMethod("value");
        String[] v= (String[]) m.invoke(rm);
        System.out.println(v[0]);
        Assert.assertTrue(v[0].equals("/haha"));
        
    }
}
