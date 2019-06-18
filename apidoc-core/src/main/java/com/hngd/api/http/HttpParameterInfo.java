package com.hngd.api.http;

import java.lang.reflect.Type;


import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.CookieParameter;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
public class HttpParameterInfo {
	
	public enum HttpParameterType {
		query(QueryParameter.class), path(PathParameter.class),
		cookie(CookieParameter.class), header(HeaderParameter.class),body(RequestBody.class);
		private HttpParameterType(Class<?> paramClass){
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
