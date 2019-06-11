package com.hngd.api.http;

import java.lang.reflect.Type;
import java.util.List;


import io.swagger.v3.oas.models.media.Schema;

public class HttpParameterInfo {
	
	public enum HttpParameterType {
		query, path, cookie, header
	}
	
	public String typeName;
	public String name;
	public boolean required;
	public HttpParameterType paramType;
	public Type paramJavaType;
	public boolean isPathVariable;
	public boolean isPrimitive;
	public String comment;

	public String type;
	public String ref;
	public boolean isCollection;
	public String format;
	public boolean isArgumentTypePrimitive;
	public Schema schema;

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
