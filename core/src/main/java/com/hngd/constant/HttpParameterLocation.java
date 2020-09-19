package com.hngd.constant;

import io.swagger.v3.oas.models.parameters.CookieParameter;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
/**
 * http参数位置,
 * @author tqd
 *
 */
public enum HttpParameterLocation {
    /**
     * 查询参数
     */
	query(QueryParameter.class), 
	/**
	 * 路径参数
	 */
	path(PathParameter.class),
	/**
	 * cookie参数
	 */
	cookie(CookieParameter.class), 
	/**
	 * 请求头参数
	 */
	header(HeaderParameter.class),
	/**
	 * request body 参数
	 */
	body(RequestBody.class);
	private HttpParameterLocation(Class<?> paramClass){
		this.paramClass=paramClass;
	}
	private Class<?> paramClass;
	public Class<?> getParamClass(){
		return paramClass;
	}
	public boolean isParameter() {
		return Parameter.class.isAssignableFrom(paramClass);
	}
}
