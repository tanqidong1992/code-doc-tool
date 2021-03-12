package com.hngd.exception;

public class SourceParseException extends ParseException {

    private static final long serialVersionUID = 1L;
    public SourceParseException(String msg,Throwable cause) {
        super(msg);
        this.initCause(cause);
    }
    
}
