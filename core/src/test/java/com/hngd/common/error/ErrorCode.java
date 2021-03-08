/**
 * Copyright (c) 2015,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * 文件名：ErrorCode.java
 * 时间：2015年12月3日 上午10:58:35
 * 作者：Administrator
 * 备注：
 */

package com.hngd.common.error;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局错误错误码说明
 * @author Administrator ， 
 *         
 */
public class ErrorCode {
	/**
	 *没有错误
	 */
	public static final Integer NO_ERROR = 0;
	/**
	 * 未知错误
	 */
	public static final Integer UNKNOWN_ERROR = 1;
	/**
	 * 名称已存在 
	 */
	public static final Integer NAME_EXIST = 2;
	/**
	 * 非法的输入参数
	 */
	public static final Integer INVALID_PARAMETER = 3;
	/**
	 * 非法的授权信息
	 */
	public static final Integer INVALID_TOKEN= 4;
	
	/**
	 * 非法操作（未授权，授权与操作不匹配）
	 */
	public static final Integer INVALID_OPERATION = 5;
	/**
	 * 设备ip已存在 
	 */
	public static final Integer DEVICE_IP_EXIST = 6;
	/**
	 * 服务器内部错误 
	 */
	public static final Integer SERVER_INTERNAL_ERROR = 7;
	/**
	 * 用户会话信息不存在 
	 */
	public static final Integer SESSION_USER_NOT_FOUND = 8;
	/**
	 * 目标不存在
	 */
	public static final Integer TARGET_NOT_FOUND = 9;
	/**
	 * 无效的认证，用户未登录，执行某些操作，会出现这个错误
	 */
	public static final Integer INVALID_CERTIFICATION = 10;
	/**
	 * 数据库操作错误
	 */
	public static final Integer DB_ERROR = 11;

	/**
	 * 用户名不能为空
	 */
	public static final Integer ACCOUNT_NOTNULL = 12;

	/**
	 * 密码不能为空
	 */
	public static final Integer CREDENTIALS_NOTNULL = 13;
	/**
	 * 账户禁用
	 */
	public static final Integer ACCOUNT_ENABLE = 14;
	/**
	 * 账户锁定
	 */
	public static final Integer ACCOUNT_LOCKED = 15;
	/**
	 * 账户过期
	 */
	public static final Integer CREDENTIALS_EXPIRED = 16;
	/**
	 * 账户未找到
	 */
	public static final Integer ACCOUNT_NOTFOUND = 17;
	/**
	 * 密码错误
	 */
	public static final Integer CREDENTIALS_INVALID = 18;

	public static final Map<Integer, String> errorDescriptions = new HashMap<>();


	static {
		errorDescriptions.put(ErrorCode.NO_ERROR, "没有错误");
		errorDescriptions.put(ErrorCode.NAME_EXIST, "名称已存在");
		errorDescriptions.put(ErrorCode.INVALID_PARAMETER, "非法的请求参数");
		errorDescriptions.put(ErrorCode.INVALID_OPERATION, "非法操作（未授权或授权与操作不匹配）");
		errorDescriptions.put(DEVICE_IP_EXIST, "设备IP已存在");
		errorDescriptions.put(SERVER_INTERNAL_ERROR, "服务器内部错误");
		errorDescriptions.put(SESSION_USER_NOT_FOUND, "用户会话信息不存在");
		errorDescriptions.put(TARGET_NOT_FOUND, "操作目标不存在");
		errorDescriptions.put(ErrorCode.INVALID_CERTIFICATION, "用户未登录");
		errorDescriptions.put(ErrorCode.DB_ERROR, "数据库操作错误");
		errorDescriptions.put(ErrorCode.ACCOUNT_NOTNULL, "用户名不能为空");
		errorDescriptions.put(ErrorCode.CREDENTIALS_NOTNULL, "密码不能为空");
		errorDescriptions.put(ErrorCode.ACCOUNT_ENABLE, "用户被禁用,请联系管理员解锁");
		errorDescriptions.put(ErrorCode.ACCOUNT_LOCKED, "密码错误次数过多,用户已被锁定");
		errorDescriptions.put(ErrorCode.CREDENTIALS_EXPIRED, "密码过期,请修改登录密码");
		errorDescriptions.put(ErrorCode.ACCOUNT_NOTFOUND, "用户未找到,请检查用户名或密码");
		errorDescriptions.put(ErrorCode.CREDENTIALS_INVALID, "密码错误");

	}

}
