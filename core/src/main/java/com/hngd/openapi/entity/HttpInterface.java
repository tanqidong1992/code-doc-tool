
package com.hngd.openapi.entity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * http接口信息
 * 
 * @author tqd
 *
 */
@Data
public class HttpInterface {

	/**
	 * Java方法名称
	 */
	public String javaMethodName;
	/**
	 * 接口路径
	 */
	public String url;
	/**
	 * 接口http请求方法
	 */
	public String httpMethod;
	/**
	 * Java方法返回类型名称
	 */
	public String javaReturnTypeName;
	/**
	 * Java方法返回类型
	 */
	public Type javaReturnType;
	/**
	 * 接口参数列表
	 */
	public List<HttpParameter> httpParameters;
	/**
	 * 接口是否包含Multipart类型，true表示是，false表示否
	 */
	public boolean isMultipart;
	/**
	 * 接口接受的http request body类型
	 */
	public List<String> consumes;
	/**
	 * 接口返回的http response body类型
	 */
	public List<String> produces;
	/**
	 * Java方法注释
	 */
	public String comment;
	/**
	 * 接口参数是否从Request Body中提取，true代表是，false表示否
	 */
	public boolean hasRequestBody = false;
	/**
	 * 接口是否已弃用，true表示是，false表示
	 */
	public Boolean deprecated;
	/**
	 * Java方法返回注释
	 */
	public String respComment;

	public HttpInterface() {
		httpParameters = new ArrayList<>();
	}

}
