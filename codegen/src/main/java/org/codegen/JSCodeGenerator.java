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

package org.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig.Interface;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.gen.SwaggerDocGenerator;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.doc.core.parse.ModuleParser;
import com.hngd.doc.core.util.TypeUtils;
import com.hngd.doc.core.web.ModuleParserTest;
import com.hngd.util.GsonUtils;
import com.hngd.web.controller.*;
 

/**
 * @author
 */
public class JSCodeGenerator
{
    public static List<Class<?>>            cls             = Arrays.asList(SystemController.class);
    public static final Map<String, String> SPRING2RETROFIT = new HashMap<String, String>();
    static
    {
        SPRING2RETROFIT.put("ResponseEntity<byte[]>", "Response");
        SPRING2RETROFIT.put("MultipartFile", "TypedFile");
    }
    public static final String[] CONTROLLER_CLASS_SRC_DIC = {
	"F:\\HNOE_TQD_JAVA\\JavaCode\\HNVMNS6000\\web\\src\\main\\java\\com\\hngd\\web\\controller" };

    static Map<String, List<ModuleInfo>> map = new HashMap<>();
 
/*    public static void useFreemake(){
    	File templateFile = new File("templates/jscode.txt");
         Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
         File dir = new File("temp");
         try {
			cfg.setDirectoryForTemplateLoading(dir);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
         cfg.setDefaultEncoding("UTF-8");
         cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
         Template t=null;
		try {
			t = new Template("jsCOdes", new FileReader(templateFile), cfg);
		} catch (IOException e1) {
			 
			e1.printStackTrace();
		}
         String filePath="F:\\HNOE_TQD_DOC\\HN\\HNFrontToEnd\\common\\js\\client.js";
          
		try (FileOutputStream fos= FileUtils.openOutputStream(new File(filePath));
				Writer out = new OutputStreamWriter(fos);){
			 t.process(map, out);
			 out.flush();
		} catch (IOException | TemplateException e1) {
			 e1.printStackTrace();
		}
 
    }*/
    public static void main(String[] args) throws IOException
    {
        useBeetl();
    }
	private static void useBeetl() throws IOException {
		File f=new File("E:\\Code\\STSCode\\hnvmns-auth\\src\\main\\java\\com\\hngd\\web\\controller\\MenuController.java");
    	List<ModuleInfo> modules=ModuleParser.parse(f);
    	//System.out.println(GsonUtils.toJsonString(modules));
    	map.put("modules", modules);
		FileResourceLoader resourceLoader = new FileResourceLoader("./beetl", "utf-8");
		Configuration cfg = Configuration.defaultConfiguration();
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
		Template t = gt.getTemplate("jscode.txt");
		//t.binding("name", "beetl");
		t.binding(map);
		String str = t.render();
		System.out.println(str);
		
	}
}
