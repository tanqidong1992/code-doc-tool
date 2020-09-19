package com.hngd.codegen.exception;

public class CodeGenerateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public CodeGenerateException(String msg,Throwable cause) {
		super(msg);
		this.initCause(cause);
	}
}
