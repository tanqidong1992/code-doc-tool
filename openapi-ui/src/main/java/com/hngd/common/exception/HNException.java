package com.hngd.common.exception;

/**
 * 自定义异常
 * @author Administrator
 */
public class HNException extends RuntimeException{
    private static final long   serialVersionUID = 1L;
    private Integer errorCode;

    public HNException(Integer errorCode, String description){
        super(description);
        this.errorCode=errorCode;
    }
    public HNException(Integer errorCode, String description,Throwable cause){
        super(description,cause);
        this.errorCode=errorCode;
    }

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
    
    
 
}
