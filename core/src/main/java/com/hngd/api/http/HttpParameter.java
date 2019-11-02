package com.hngd.api.http;

import java.lang.reflect.Type;

import com.hngd.constant.HttpParameterType;

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
 
}
