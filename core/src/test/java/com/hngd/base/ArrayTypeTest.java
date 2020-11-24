package com.hngd.base;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

public class ArrayTypeTest {
    private Byte[] a;
	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		Field field=ArrayTypeTest.class.getDeclaredField("a");
		Type type=new TypeToken<Byte[]>() {}.getType();
		type=field.getGenericType();
		if(((Class<?>)type).isArray()) {
			System.out.println("array");
		}
		System.out.print(type.getClass());
	}
}
