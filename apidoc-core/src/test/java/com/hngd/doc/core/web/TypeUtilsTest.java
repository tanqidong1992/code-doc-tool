package com.hngd.doc.core.web;

import java.lang.reflect.Type;

import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.reflect.TypeToken;
import com.hngd.doc.core.util.TypeUtils;

public class TypeUtilsTest {

	@Test
	public void testMultipart() {
		
		Type parameterType=new TypeToken<MultipartFile[]>() {}.getType();
		Boolean result=TypeUtils.isMultipartType(parameterType);
		System.out.println(result);
		parameterType=new TypeToken<MultipartFile>() {}.getType();
		 result=TypeUtils.isMultipartType(parameterType);
		System.out.println(result);
	}
}
