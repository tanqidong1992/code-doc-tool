package com.hngd.doc.core;

import java.util.Date;
import java.util.List;

/**
 * 类成员函数信息
 * @author hnoe-dev-tqd
 *
 */
public class MethodInfo {
    /**
     * 方法名称
     */
	public String methodName;
	/**
	 * 方法注释
	 */
	public String comment;
	/**
	 * 返回注释
	 */
	public String retComment;
	/**
	 * 参数信息列表
	 */
	public List<ParameterInfo> parameters;
	/**
	 * 创建时间字符串(已被格式化)
	 */
	public String createTimeStr;
	/**
	 * 创建时间
	 */
	public Date createTime;
 
}



