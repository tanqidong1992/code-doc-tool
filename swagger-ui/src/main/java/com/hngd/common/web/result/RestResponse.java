/**
 * @FileName RestResponseEntity.java
 * @Author tqd
 * @Date 2015年9月3日 下午11:41:01
 * @description
 */
package com.hngd.common.web.result;

import com.hngd.common.error.ErrorCode;

/**
 * http请求返回统一结构体
 * @author tqd
 * @param <T>
 */
public class RestResponse<T> {
	/**
	 * 附加属性
	 */
	private Object extra;
	/**
	 *请求结果描述
	 */
	private String description;
	/**
	 *请求结果代码 详情参考{@link ErrorCode}
	 */
	private Integer errorCode;
	/**
	 *返回数据的数量
	 */
	private int dataSize;
	/**
	 *数据
	 */
	private T data;
	/**
	 * 调用是否成功,true表示成功,false表示失败
	 */
    private Boolean success;
	public Object getExtra() {
		return extra;
	}

	public void setExtra(Object extra) {
		this.extra = extra;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
		if(ErrorCode.NO_ERROR.equals(errorCode)) {
			this.success=true;
		}else {
			this.success=false;
		}
	}
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	public int getDataSize() {
		return dataSize;
	}
	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	public Boolean getSuccess() {
		return success;
	}
	public Boolean isSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	 
}
