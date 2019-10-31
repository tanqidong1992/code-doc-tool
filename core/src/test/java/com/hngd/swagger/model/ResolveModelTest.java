package com.hngd.swagger.model;

import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hngd.common.util.GsonUtils;
import com.hngd.doc.core.gen.OpenAPITool;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class ResolveModelTest {
    static  class A{
    	private int a;
    	private int b;
    	private List<B> bs;
    	private B[] barray;
		public int getA() {
			return a;
		}
		public void setA(int a) {
			this.a = a;
		}
		public int getB() {
			return b;
		}
		public void setB(int b) {
			this.b = b;
		}
		public List<B> getBs() {
			return bs;
		}
		public void setBs(List<B> bs) {
			this.bs = bs;
		}
		public B[] getBarray() {
			return barray;
		}
		public void setBarray(B[] barray) {
			this.barray = barray;
		}
    	
    }
    static class B{
    	private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
    	
    }
    
    static class C extends B{
    	 private String age;

		public String getAge() {
			return age;
		}

		public void setAge(String age) {
			this.age = age;
		}
    	 
    	 
    }
	public static void main(String[] args) throws JsonProcessingException {
		Type type=C.class;
		OpenAPI swagger=new OpenAPI();
		OpenAPITool.resolveType(type, swagger);
	 
        Json.prettyPrint(swagger);
	}
	public static String toJson(OpenAPI openAPI) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		return mapper.writeValueAsString(openAPI);
	}
}
