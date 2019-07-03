
package com.hngd.api.http;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
/**
 * http接口信息
 * @author tqd
 *
 */
public class HttpInterfaceInfo{
	
/**
 * Java方法名称
 */
	public String methodName;
	/**
	 * 接口路径
	 */
	public String methodUrl;
	/**
	 * 接口http请求方法
	 */
	public String httpMethod;
	/**
	 * Java方法返回类型名称
	 */
	public String retureTypeName;
	/**
	 * Java方法返回类型
	 */
	public Type retureType;
	/**
	 * 接口参数列表
	 */
	public List<HttpParameterInfo> parameterInfos;
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
     * 接口说明
     */
	public String comment;
	/**
	 * 接口参数是否从Request Body中提取，true代表是，false表示否
	 */
	public boolean hasRequestBody=false;
	/**
	 * 接口是否已弃用，true表示是，false表示
	 */
	public Boolean deprecated;
	public HttpInterfaceInfo() {
		parameterInfos = new ArrayList<>();
	}
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<HttpParameterInfo> getParameterInfos() {
		return parameterInfos;
	}

	public void setParameterInfos(List<HttpParameterInfo> parameterInfos) {
		this.parameterInfos = parameterInfos;
	}



	/**
	 * @return the methodName
	 */
	public final String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public final void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the methodUrl
	 */
	public final String getMethodUrl() {
		return methodUrl;
	}

	/**
	 * @param methodUrl the methodUrl to set
	 */
	public final void setMethodUrl(String methodUrl) {
		this.methodUrl = methodUrl;
	}

	/**
	 * @return the requestType
	 */
	public final String getRequestType() {
		return httpMethod;
	}

	/**
	 * @param requestType the requestType to set
	 */
	public final void setRequestType(String requestType) {
		this.httpMethod = requestType;
	}

	/**
	 * @return the retureTypeName
	 */
	public final String getRetureTypeName() {
		return retureTypeName;
	}

	/**
	 * @param retureTypeName the retureTypeName to set
	 */
	public final void setRetureTypeName(String retureTypeName) {
		this.retureTypeName = retureTypeName;
	}

	/**
	 * @return the retureType
	 */
	public final Type getRetureType() {
		return retureType;
	}

	/**
	 * @param retureType the retureType to set
	 */
	public final void setRetureType(Type retureType) {
		this.retureType = retureType;
	}

	/**
	 * @return the parameterNames
	 */
	public final List<HttpParameterInfo> getParameterNames() {
		return parameterInfos;
	}

	/**
	 * @param parameterNames the parameterNames to set
	 */
	public final void setParameterNames(List<HttpParameterInfo> parameterNames) {
		this.parameterInfos = parameterNames;
	}

	/**
	 * @return the isMultipart
	 */
	public final boolean isMultipart() {
		return isMultipart;
	}

	/**
	 * @param isMultipart the isMultipart to set
	 */
	public final void setMultipart(boolean isMultipart) {
		this.isMultipart = isMultipart;
	}

	/**
	 * @return the consumes
	 */
	public final List<String> getConsumes() {
		return consumes;
	}

	/**
	 * @param consumes the consumes to set
	 */
	public final void setConsumes(List<String> consumes) {
		this.consumes = consumes;
	}

	/**
	 * @return the produces
	 */
	public final List<String> getProduces() {
		return produces;
	}

	/**
	 * @param produces the produces to set
	 */
	public final void setProduces(List<String> produces) {
		this.produces = produces;
	}

	public boolean isHasRequestBody() {
		return hasRequestBody;
	}

	public void setHasRequestBody(boolean hasRequestBody) {
		this.hasRequestBody = hasRequestBody;
	}

	public Boolean getDeprecated() {
		return deprecated;
	}

	public void setDeprecated(Boolean deprecated) {
		this.deprecated = deprecated;
	}

}
