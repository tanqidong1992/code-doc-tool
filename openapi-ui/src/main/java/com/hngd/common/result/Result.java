package com.hngd.common.result;

import java.io.Serializable;

import com.hngd.common.error.ErrorCode;

/**
 * 业务逻辑层调用返回结果
 * 
 * @author Administrator
 */
public class Result<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 错误码
	 */
	private Integer errorCode;
	/**
	 * 数据
	 */
	private T data;
	/**
	 * 错误信息描述
	 */
	private String description;

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSuccess() {
		return ErrorCode.NO_ERROR.equals(errorCode);
	}

}
