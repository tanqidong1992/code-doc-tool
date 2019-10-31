package com.hngd.api.http;

import java.lang.reflect.Type;

import com.hngd.constant.HttpParameterType;

import io.swagger.v3.oas.models.media.Schema;
/**
 * http接口参数信息
 * @author tqd
 *
 */
public class HttpParameterInfo {
	/**
	 * Java类型名称
	 */
	public String typeName;
	/**
	 * 参数名称
	 */
	public String name;
	/**
	 * 是否必须
	 */
	public boolean required;
	/**
	 * 参数位置类型
	 */
	public HttpParameterType paramType;
	/**
	 * 参数Java类型
	 */
	public Type paramJavaType;
	/**
	 * 是否路径参数,目前只有生成js代码用到，后期考虑去掉
	 */
	@Deprecated
	public boolean isPathVariable;
	/**
	 * 是否基础类型，
	 */
	public boolean isPrimitive;
	/**
	 * 说明，对应Java注释
	 */
	public String comment;
    /**
     * Swagger 参数类型
     */
	public String type;
	/**
	 * Swagger 引用路径
	 */
	public String ref;
	/**
	 * 是否集合
	 */
	public boolean isCollection;
	/**
	 * 类型参数
	 */
	public Class<?> parameterizedType;
	/**
	 * Swagger format
	 */
	public String format;
	/**
	 * Swagger schema
	 */
	public Schema<?> schema;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public HttpParameterType getParamType() {
		return paramType;
	}

	public void setParamType(HttpParameterType paramType) {
		this.paramType = paramType;
	}

	public boolean isPathVariable() {
		return isPathVariable;
	}

	public void setPathVariable(boolean isPathVariable) {
		this.isPathVariable = isPathVariable;
	}

	public boolean isPrimitive() {
		return isPrimitive;
	}

	public void setPrimitive(boolean isPrimitive) {
		this.isPrimitive = isPrimitive;
	}

	public Type getParamJavaType() {
		return paramJavaType;
	}

	public void setParamJavaType(Type paramJavaType) {
		this.paramJavaType = paramJavaType;
	}
	

}
