package com.hngd.codegen;

import java.util.List;

import com.hngd.openapi.entity.ModuleInfo;

/**
 * 代码模板
 * @author tqd
 *
 */
public abstract class CodeTemplate {

	protected String name;
	protected String templateFilePath;
	protected String templateEngine;
	public CodeTemplate(String name,String templateFilePath,String templateEngine) {
		this.name=name;
		this.templateEngine=templateEngine;
		this.templateFilePath=templateFilePath;
	}
	public abstract String generate(List<ModuleInfo> modules,String serviceUrl);
}
