package com.hngd.doc.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * https://swagger.io/specification/#referenceObject
 * @author hnoe-dev-tqd
 *
 */
@Data
@Slf4j
public class SimpleTypeFormat {
    /**
               * 类型
     */
	private String type;
	/**
	    * 格式
	 */
	private String format;
	
	public SimpleTypeFormat(String type, String format) {
		this.type = type;
		this.format = format;
	}
    public static SimpleTypeFormat newTypeFormat(String type,String format) {
    	return new SimpleTypeFormat(type,format);
    }
	public static final SimpleTypeFormat INT32=newTypeFormat("integer", "int32");
	public static final SimpleTypeFormat INT64=newTypeFormat("integer", "int64");
	
	public static final SimpleTypeFormat FLOAT=newTypeFormat("number", "float");
	public static final SimpleTypeFormat DOUBLE=newTypeFormat("number", "double");
	
	public static final SimpleTypeFormat BOOLEAN=newTypeFormat("boolean", null);
	
	public static final SimpleTypeFormat SIMPLE_STRING=newTypeFormat("string", null);
	
	public static final SimpleTypeFormat BYTE_STRING=newTypeFormat("string", "byte");
	public static final SimpleTypeFormat BINARY_STRING=newTypeFormat("string", "binary");
	public static final SimpleTypeFormat DATE_STRING=newTypeFormat("string", "date");
	public static final SimpleTypeFormat DATE_TIME_STRING=newTypeFormat("string", "date-time");
	public static final SimpleTypeFormat PASSWORD_STRING=newTypeFormat("string", "password");
	
	/**
	 * Java简单类型转OpenAPI 简单类型
	 * @param type
	 * @return
	 */
	public static SimpleTypeFormat convert(Class<?> type) {
		if(int.class.equals(type) || Integer.class.equals(type)) {
			return INT32;
		}else if(long.class.equals(type) || Long.class.equals(type)) {
			return INT64;
		}else if(float.class.equals(type) || Float.class.equals(type)) {
			return FLOAT;
		}else if(double.class.equals(type) || Double.class.equals(type)) {
			return DOUBLE;
		}else if(boolean.class.equals(type) || Boolean.class.equals(type)) {
			return BOOLEAN;
		}else if(byte.class.equals(type) || Byte.class.equals(type)) {
			return INT32;
		}else if(Short.class.equals(type) || Short.class.equals(type)) {
			return INT32;
		}else if(String.class.equals(type)) {
			//TODO string ??
			return SIMPLE_STRING;
		}else {
			//TODO XXX
			log.error("could covert type:"+type.getName());
			return null;
		}
	}
	
}
