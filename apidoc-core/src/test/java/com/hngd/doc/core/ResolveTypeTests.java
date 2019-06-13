package com.hngd.doc.core;

import java.util.List;

import com.hngd.doc.core.gen.OpenAPITool;

import io.swagger.v3.oas.models.OpenAPI;

public class ResolveTypeTests {

	public static void main(String[] args) {
		OpenAPI swagger=new OpenAPI();
		// TODO Auto-generated method stub
		OpenAPITool.resolveType(A.class, swagger);
System.out.println(swagger);
	}
	
	public static class A{
		private List<B> bs;
		private int a;
		private Double c;
	}

	public static class B{
		
	}

}
