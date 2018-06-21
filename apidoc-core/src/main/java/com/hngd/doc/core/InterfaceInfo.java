
package com.hngd.doc.core;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

public class InterfaceInfo
{
    public InterfaceInfo()
    {
        parameterNames = new ArrayList<>();
        parameterTypes = new ArrayList<>();
    }
    public String                     methodName;
    public String                     methodUrl;
    public String                     requestType;
    public String                     retureTypeName;
    public Type                       retureType;
    public List<RequestParameterInfo> parameterNames;
    public boolean                    isMultipart;
    public List<Type>                 parameterTypes;
    public List<String>               consumes;
    public List<String>               produces;

    public static class RequestParameterInfo
    {
        public String    name;
        public boolean   required;
        public ParamType paramType;
    }

    public enum ParamType
    {
        REQUEST, PATH,
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
		return requestType;
	}

	/**
	 * @param requestType the requestType to set
	 */
	public final void setRequestType(String requestType) {
		this.requestType = requestType;
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
	public final List<RequestParameterInfo> getParameterNames() {
		return parameterNames;
	}

	/**
	 * @param parameterNames the parameterNames to set
	 */
	public final void setParameterNames(List<RequestParameterInfo> parameterNames) {
		this.parameterNames = parameterNames;
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
	 * @return the parameterTypes
	 */
	public final List<Type> getParameterTypes() {
		return parameterTypes;
	}

	/**
	 * @param parameterTypes the parameterTypes to set
	 */
	public final void setParameterTypes(List<Type> parameterTypes) {
		this.parameterTypes = parameterTypes;
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
    
    
}
