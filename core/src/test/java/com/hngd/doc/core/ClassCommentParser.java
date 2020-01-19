package com.hngd.doc.core;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.source.ParserContext;
import com.hngd.web.controller.RoleController;

public class ClassCommentParser {

	@Test
	public void testParse(){
		
		File f=new File("../core-test/src/main/java/com/hngd/web/controller/RoleController.java");
		ParserContext pc=new ParserContext();
		pc.parse(f);
		pc.printResult();
		Class<?> clazz=RoleController.class;
		String c=pc.getClassComment(clazz);
		Assert.assertTrue(!StringUtils.isEmpty(c));
		Method[] methods=clazz.getDeclaredMethods();
		for(int i=0;i<methods.length;i++) {
			Method method=methods[i];
			Optional<MethodInfo> mi=pc.getMethodInfo(method);
			Assert.assertTrue(StringUtils.isNotEmpty(mi.get().getComment()));
			mi.get().getParameters()
			.forEach(pi->{
				Assert.assertTrue(StringUtils.isNotEmpty(pi.getComment()));
			});
		}
	}
}
