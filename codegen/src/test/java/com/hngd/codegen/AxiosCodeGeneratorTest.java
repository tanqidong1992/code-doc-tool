package com.hngd.codegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.hngd.codegen.AxiosCodeGenerator;
import com.hngd.codegen.constant.TestConstants;
import com.hngd.openapi.entity.ModuleInfo;
import com.hngd.parser.source.SourceParserContext;
import com.hngd.parser.spring.ClassParser;
import com.hngd.web.controller.RoleController;

public class AxiosCodeGeneratorTest {

	public static void main(String[] args) throws IOException {

		AxiosCodeGenerator acg = new AxiosCodeGenerator();
		SourceParserContext parserContext = new SourceParserContext();
		parserContext.initSource(new File(TestConstants.JAVA_SRC_ROOT));
		parserContext.getCommentStore().print();
		ClassParser cp = new ClassParser(parserContext.getCommentStore());
		Optional<ModuleInfo> omi = cp.parseModule(RoleController.class);
		List<ModuleInfo> modules = new ArrayList<>();
		if (omi.isPresent()) {
			modules.add(omi.get());
		}
		String serviceUrl = "/";
		String s = acg.generate(modules, serviceUrl);
		System.out.println(s);

	}
}
