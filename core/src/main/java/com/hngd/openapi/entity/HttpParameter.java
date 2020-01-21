package com.hngd.openapi.entity;

import java.lang.reflect.Type;

import com.hngd.constant.HttpParameterLocation;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Data;
/**
 * http接口参数信息
 * @author tqd
 *
 */
@Data
public class HttpParameter {
	/**
	 * Java类型名称
	 */
	public String javaTypeName;
	/**
	 * 参数Java类型
	 */
	public Type javaType;
	/**
	 * 参数java类型
	 */
	public Class<?> javaParameterizedType;
	/**
	 * 参数名称
	 */
	public String name;
	/**
	 * 是否必须
	 */
	public boolean required;
	/**
	 * 参数位置
	 */
	public HttpParameterLocation location;
	/**
	 * 是否路径参数,目前只有生成js代码用到
	 */
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
     * 参数类型(openapi)
     */
	public String openapiType;
	/**
	 * openapi schema引用路径
	 */
	public String ref;
	/**
	 * 是否集合
	 */
	public boolean isCollection;
	/**
	 * openapi format
	 */
	public String openapiFormat;
	/**
	 * openapi schema
	 */
	public Schema<?> schema;
	
	/**
	 * 参数在Java方法中的位置
	 */
	public Integer indexInJavaMethod;
 
}
