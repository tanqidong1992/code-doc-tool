package com.hngd.openapi;

import java.lang.reflect.Type;
import java.util.List;

import io.swagger.v3.oas.models.media.ArraySchema;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.reflect.TypeToken;
import com.hngd.common.util.GsonUtils;
import com.hngd.openapi.entity.HttpParameter;

public class OpenAPIToolTest {

	@Test
	public void test() {
		
		HttpParameter pc=new HttpParameter();
		Type type=new TypeToken<List<String>>() {
		}.getType();
		OpenAPITool.resolveParameterInfo(pc, type);
		System.out.println(pc);
		Assert.assertTrue(pc.isCollection);
		Assert.assertTrue(pc.getSchema().getType().equals("array"));
		Assert.assertTrue(((ArraySchema)pc.schema).getItems().getType().equals("string"));
	}
}
