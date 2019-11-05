package com.hngd.exception;

import java.lang.reflect.Method;

public class ClassParseException extends ParseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ClassParseException(String msg,Throwable cause) {
		super(msg);
		this.initCause(cause);
	}
	
	public ClassParseException(Class<?> clazz,Throwable cause) {
	    super("Parse class:"+clazz.getName()+" failed");
	    this.initCause(cause);
	}
	public ClassParseException(Class<?> clazz,Method method,Throwable cause) {
	    super("Parse method:"+clazz.getName()+"."+method.getName()+" failed");
	    this.initCause(cause);
	}
}
