package com.hngd.exception;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

public class ClassParseException extends ParseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ClassParseException(String msg,Throwable cause) {
		super(msg);
		this.initCause(cause);
	}
	
	public ClassParseException(Class<?> clazz,String msg,Throwable cause) {
	    super("Parse class:"+clazz.getName()+" failed!"+msg);
	    this.initCause(cause);
	}
	public ClassParseException(Class<?> clazz,Executable method,String msg,Throwable cause) {
	    super("Parse method:"+clazz.getName()+"."+method.getName()+" failed!"+msg);
	    this.initCause(cause);
	}
	public ClassParseException(Class<?> clazz,Executable method,Parameter parameter,String msg,Throwable cause) {
	    super("Parse parameter:"+clazz.getName()+"."+method.getName()+"."+parameter.getName()+" failed!"+msg);
	    this.initCause(cause);
	}
	
	
	public ClassParseException(Class<?> clazz,Throwable cause) {
	   this(clazz, "", cause);
	}
	public ClassParseException(Class<?> clazz,Executable method,Throwable cause) {
		 this(clazz,method, "", cause);
	}
	public ClassParseException(Class<?> clazz,Executable method,Parameter parameter,Throwable cause) {
		 this(clazz,method,parameter, "", cause);
	}
	
	public static void throwClassParseException(Class<?> clazz,String msg, Throwable cause) {
		throw new ClassParseException(clazz, msg, cause);
	}
	public static void throwMethodParseException(Executable method,String msg, Throwable cause) {
		Class<?> clazz=method.getDeclaringClass();
		throw new ClassParseException(clazz, method,msg, cause);
	}
	public static void throwParameterParseException(Parameter parameter,String msg, Throwable cause) {
		Executable method=parameter.getDeclaringExecutable();
		Class<?> clazz=method.getDeclaringClass();
		throw new ClassParseException(clazz, method,parameter,msg, cause);
	}
	
	
	public static void throwClassParseException(Class<?> clazz,Throwable cause) {
		throw new ClassParseException(clazz, "", cause);
	}
	public static void throwMethodParseException(Executable method,Throwable cause) {
		Class<?> clazz=method.getDeclaringClass();
		throw new ClassParseException(clazz, method,"", cause);
	}
	public static void throwParameterParseException(Parameter parameter,Throwable cause) {
		Executable method=parameter.getDeclaringExecutable();
		Class<?> clazz=method.getDeclaringClass();
		throw new ClassParseException(clazz, method,parameter,"", cause);
	}
}
