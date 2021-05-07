package com.hngd.common.result;

import com.hngd.common.error.ErrorCode;

/**
 * @author Administrator
 */
public class Results {
	/**
	 * 创建一个成功的返回结果
	 * @param <T>
	 * @param data 返回数据
	 * @return
	 */
	public static <T> Result<T> newSuccessResult(T data) {
		return newSuccessResult(data, null);
	}
    /**
     * 创建一个成功的返回结果
     * @param <T>
     * @param data 数据
     * @param description 描述
     * @return
     */
	public static <T> Result<T> newSuccessResult(T data, String description) {
		Result<T> r = new Result<T>();
		r.setErrorCode(ErrorCode.NO_ERROR);
		r.setData(data);
		r.setDescription(description);
		return r;
	}
    /**
     * 创建一个失败的返回结果
     * @param <T>
     * @param errorCode 错误码 {@link ErrorCode}
     * @param description 描述
     * @return
     */
	public static <T> Result<T> newFailResult(Integer errorCode, String description) {
		Result<T> r = new Result<T>();
		r.setDescription(description);
		r.setErrorCode(errorCode);
		return r;
	}
}
