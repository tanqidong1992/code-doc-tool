package com.hngd.doc.core;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.hngd.openapi.OpenAPITool;
import com.hngd.parser.source.ParserContext;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Data;

public class ResolveTypeTests {

	@Test
	public void main() {
		OpenAPI openAPI=new OpenAPI();
		ParserContext parserContext=new ParserContext();
		parserContext.parse(new File("./src/test/java/com/hngd/doc/core/ResolveTypeTests.java"));
		parserContext.printResult();
		OpenAPITool opt=new OpenAPITool(openAPI, parserContext);
		opt.resolveType(A.class, openAPI);
        System.out.println(Json.pretty(openAPI));
        Schema<?> schema=openAPI.getComponents().getSchemas().get("A");
        
        Map<String, Schema> ps=schema.getProperties();
        Assert.assertTrue(ps.get("bs").getDescription().trim().equals("field bs"));
        Assert.assertTrue(ps.get("a").getDescription().trim().equals("field a"));
        Assert.assertTrue(ps.get("c").getDescription().trim().equals("field c"));
	}
	/**
	 * class A
	 * @author tqd
	 *
	 */
	@Data
	public static class A{
		/**
		 * field bs
		 */
		private List<B> bs;
		/**
		 * field a
		 */
		private int a;
		/**
		 * field c
		 */
		private Double c;
	}
    /**
     * class B
     * @author tqd
     *
     */
	public static class B{
		
	}

}
