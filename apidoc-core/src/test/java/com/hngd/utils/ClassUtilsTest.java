package com.hngd.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.hngd.doc.core.util.ClassUtils;

public class ClassUtilsTest {

	
	static class A{
		public A(int a,int b) {
			
		}
		public void sayA(int c) {
			
		}
	}
	@Test
	public void test() throws NoSuchMethodException, SecurityException {
		
		Method m=A.class.getDeclaredMethod("sayA", int.class);
		String methodIdentifier=ClassUtils.getMethodIdentifier(m);
		System.out.println(methodIdentifier);
		Assert.assertTrue(methodIdentifier.endsWith("A.sayA"));
		Parameter p1=m.getParameters()[0];
		String parameterIdentifier=ClassUtils.getParameterIdentifier(p1);
		System.out.println(parameterIdentifier);
		Assert.assertTrue(methodIdentifier.endsWith("A.sayA.c") || methodIdentifier.endsWith("A.sayA.arg0"));
	    A.class.getConstructor(int.class,int.class);
	
	}
}
