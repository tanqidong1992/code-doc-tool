/**
 * Copyright (c) 2015,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：JSCodeGenerator.java
 * @时间：2016年8月28日 上午10:57:31
 * @作者：
 * @备注：
 * @版本:
 */

package com.hngd.codegen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.springframework.util.CollectionUtils;

import com.hngd.codegen.exception.CodeGenerateException;
import com.hngd.constant.Constants;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.openapi.entity.ModuleInfo;
import com.hngd.parser.clazz.ClassParser;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.source.ParserContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tqd
 */
@Slf4j
public class AjaxCodeGenerator extends CodeTemplate{
    public AjaxCodeGenerator() {
		super("axios", "js-api-ajax.btl", TemplateEngines.BEETL);
		
	}
    @Override
	public String generate(List<ModuleInfo> modules,String serviceUrl){
    	 
    	if(CollectionUtils.isEmpty(modules)){
    		return null;
    	}
    	Map<String, Object> map = new HashMap<>();
    	map.put("serviceUrl", serviceUrl);
    	map.put("modules", modules);
    	ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/code/template", Constants.DEFAULT_CHARSET_NAME);
		Configuration cfg=null;
		try {
			cfg = Configuration.defaultConfiguration();
		} catch (IOException e) {
			throw new CodeGenerateException("", e);
		}
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
		Template t = gt.getTemplate(templateFilePath);
		t.binding(map);
		String str = t.render();
		return str;
    	
    }
 
     
}
