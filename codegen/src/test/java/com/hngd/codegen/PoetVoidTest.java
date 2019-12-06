package com.hngd.codegen;


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import rx.Observable;

public class PoetVoidTest {
    public static void test1() {
    	
    }
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Method m=PoetVoidTest.class.getMethod("test1");
		Class<?> v=m.getReturnType();
		Type returnType=new ParameterizedType() {
			
			@Override
			public Type getRawType() {
				// TODO Auto-generated method stub
				return Observable.class;
			}
			
			@Override
			public Type getOwnerType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Type[] getActualTypeArguments() {
				// TODO Auto-generated method stub
				return new Type[] {v};
			}
		};
		MethodSpec methodSpec=MethodSpec
				.methodBuilder("test")
				.addModifiers(Modifier.PUBLIC)
				.returns(returnType)
				.build();
		TypeSpec ts=TypeSpec.classBuilder("TestVoid")
				.addMethod(methodSpec)
				.build();
		
		JavaFile jf=JavaFile.builder("com.tqd.test", ts).build();
		String s=jf.toString();
		System.out.println(s);

	}

}
