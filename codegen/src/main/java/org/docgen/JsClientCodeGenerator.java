/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：JsClientCodeGenerator.java
 * @时间：2017年8月10日 下午3:35:10
 * @作者：
 * @备注：
 * @版本:
 */
package org.docgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;

import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.gen.SwaggerDocGenerator;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.util.GsonUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * @author
 */
public class JsClientCodeGenerator {
	static {
		PropertyConfigurator.configure("./log4j.properties");
	}

	/**
	 * controller类源代码所在位置
	 */
	public static final String[] CONTROLLER_CLASS_SRC_DIC = {
			"F:\\HNOE_TQD_JAVA\\JavaCode\\HNVMNS6000\\web\\src\\main\\java\\com\\hngd\\web\\controller" };
	private static final String HOST_NAME = "http://192.168.0.156:8080/web";

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		Map<String, List<ModuleInfo>> map = new HashMap<>();
		ControllerClassCommentParser.init(CONTROLLER_CLASS_SRC_DIC[0]);
		List<Class<?>> clazzes = SwaggerDocGenerator.getClassBelowPacakge("com.hngd.web.controller");
		List<ModuleInfo> modules = new LinkedList<>();
		clazzes.forEach(clazz -> {
			ModuleInfo mi = SwaggerDocGenerator.processClass(clazz);
			modules.add(mi);
		});

		map.put("modules", modules);
		File templateFile = new File("templates/jscode2.txt");
		// ControllerClassParser.init();
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
		File dir = new File("temp");
		cfg.setDirectoryForTemplateLoading(dir);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		Template t = new Template("jsCOdes", new FileReader(templateFile), cfg);
		// FileOutputStream fos = FileUtils.openOutputStream(
		// new
		// File("E:\\HNOE_TQD_DOC\\HN\\web-test\\src\\main\\java\\com\\hngd\\retrofit\\WebInterface.java"));
		String filePath = "./output/test2.js";
		FileOutputStream fos = FileUtils.openOutputStream(new File(filePath));
		Writer out = new OutputStreamWriter(fos);
		try {
			t.process(map, out);
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.flush();
		out.close();

	}
}
