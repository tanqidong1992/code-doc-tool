package com.hngd.common.web;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.common.error.ErrorCode;
import com.hngd.common.result.Result;
import com.hngd.common.web.result.RestResponse;

/**
 * @author Administrator
 */
public class RestResponses {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(RestResponses.class);

	/**
	 * 创建一个Http请求的返回结果对象
	 * @param errorCode   错误代码 没有错误则为0
	 * @param description 请求结果描述
	 * @param data        数据,数量为0时,可以为null
	 * @param extra       额外的数据,主要用于分页查询,返回的总页数,可以为null
	 * @return 结果对象
	 * @作者:Administrator
	 * @时间:2015年11月12日 下午3:05:49
	 * @备注:
	 */
	private static <T> RestResponse<T> newResponse(Integer errorCode, String description, T data, Object extra) {
		RestResponse<T> re = new RestResponse<T>();
		re.setData(data);
		int dataSize = dataSize(data);
		re.setDataSize(dataSize);
		re.setErrorCode(errorCode);
		re.setDescription(description);
		re.setExtra(extra);
		return re;
	}

	/**
	 * 创建一个Http请求操作成功的返回结果对象
	 * 
	 * @param description 请求结果描述
	 * @param data        数据
	 * @param extra       额外的数据,主要用于分页查询,返回的总页数,可以为null
	 * @return 结果对象
	 * @作者:Administrator
	 * @时间:2015年11月12日 下午3:12:29
	 * @备注:
	 */
	public static <T> RestResponse<T> newSuccessResponse(String description, T data, Object extra) {
		return newResponse(ErrorCode.NO_ERROR, description, data, extra);
	}

	/**
	 * 创建一个Http请求,操作成功的返回结果对象
	 * 
	 * @param description 请求结果描述
	 * @param data        数据
	 * @return 结果对象
	 * @作者:Administrator
	 * @时间:2015年11月12日 下午3:12:29
	 * @备注:
	 */
	public static <T> RestResponse<T> newSuccessResponse(String description, T data) {
		return newResponse(ErrorCode.NO_ERROR, description, data, null);
	}
	public static <T> RestResponse<T> newSuccessResponse(T data) {
		return newResponse(ErrorCode.NO_ERROR, "", data, null);
	}
	/**
	 * 创建一个Http请求,操作失败的返回结果对象
	 * 
	 * @param description 请求结果描述
	 * @param errorCode   错误代码
	 * @return
	 * @作者:Administrator
	 * @时间:2015年11月12日 下午3:14:08
	 * @备注:
	 */
	public static <T> RestResponse<T> newFailResponse(Integer errorCode, String description) {
		return newResponse(errorCode, description, null, null);
	}

	/**
	 * 创建一个Http请求,操作失败的返回结果对象
	 * 
	 * @param description 请求结果描述
	 * @param errorCode   错误代码
	 * @return
	 * @作者:Administrator
	 * @时间:2015年11月12日 下午3:14:08
	 * @备注:
	 */
	public static <T> RestResponse<T> newFailResponse(String description, Integer errorCode) {
		return newResponse(errorCode, description, null, null);
	}

	/**
	 * @param string
	 * @return
	 * @作者:Administrator
	 * @时间:2016年5月11日 下午3:14:35
	 * @备注:
	 */
	public static RestResponse<Void> newSuccessResponse(String string) {
		return newSuccessResponse(string, null);
	}
    /**
     * 创建一个数据为空的成功http响应对象
     * @return
     */
	public static RestResponse<Void> newSuccessResponse() {
		return newSuccessResponse("");
	}
    /**
     * 根据result对象创建一个http响应对象
     * @param <T> result对象
     * @param result
     * @return
     */
	public static <T> RestResponse<T> newResponseFromResult(Result<T> result) {
		if (result.isSuccess()) {
			T data = result.getData();
			return newSuccessResponse(result.getDescription(), data);
		} else {
			return newFailResponse(result.getErrorCode(), result.getDescription());
		}
	}

	/**
	 * 计算数据大小，数组，集合，map
	 * @param <T>
	 * @param data
	 * @since 0.0.3
	 * @return
	 */
	private static <T> int dataSize(T data) {
		if (data == null) {
			return 0;
		}
		if (data.getClass().isArray()) {
			return Array.getLength(data);
		}
		if (data instanceof Collection<?>) {
			return ((Collection<?>) data).size();
		}
		if (data instanceof Map<?, ?>) {
			return ((Map<?, ?>) data).size();
		}
		return 1;
	}
	
    /**
     * 创建一个失败的响应
     * @param <T>
     * @param result 错误信息实体
     * @return
     * @since 2.0.0
     */
	public static <T> RestResponse<T> newFailFromResult(Result<?> result) {
		return newResponse(result.getErrorCode(), result.getDescription(), null, null);
	}
}
