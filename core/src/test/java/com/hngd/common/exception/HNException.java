/**
 * Copyright (c) 2015,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：HNException.java
 * @时间：2016年6月2日 下午2:30:15
 * @作者：Administrator
 * @备注：
 * @版本:
 */

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
