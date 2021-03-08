package com.hngd.utils;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/haha")
public class RestClassUtilsTest {

    @Test
    public void testGetHHH() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        
        RequestMapping rm=RestClassUtilsTest.class.getAnnotation(RequestMapping.class);
        String url=RestClassUtils.extractUrl(rm);
        assertEquals("/haha", url);
        
    }
    
    
}