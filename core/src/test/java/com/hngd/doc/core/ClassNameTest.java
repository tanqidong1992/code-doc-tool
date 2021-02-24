package com.hngd.doc.core;

public class ClassNameTest {

    public static void main(String []args) {
        Byte[] a=new Byte[1];
        Class<?> clazz=a.getClass();
        System.out.println("name:"+clazz.getName());
        System.out.println("simpleName:"+clazz.getSimpleName());
        System.out.println("canonicalName:"+clazz.getCanonicalName());
        System.out.println("typeName:"+clazz.getTypeName());
    }
}
