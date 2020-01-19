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

import com.hngd.openapi.entity.HttpParameter;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.entity.ModuleInfo;
import com.hngd.parser.source.ParserContext;
import com.hngd.parser.source.SourceParser;

 
 

/**
 * @author
 */
public class JSCodeGenerator
{
    public static final Map<String, String> SPRING2RETROFIT = new HashMap<String, String>();
    static
    {
        SPRING2RETROFIT.put("ResponseEntity<byte[]>", "Response");
        SPRING2RETROFIT.put("MultipartFile", "TypedFile");
    }
    public static final String[] CONTROLLER_CLASS_SRC_DIC = {
	"F:\\HNOE_TQD_JAVA\\JavaCode\\HNVMNS6000\\web\\src\\main\\java\\com\\hngd\\web\\controller" };

    static Map<String, Object> map = new HashMap<>();
    
 
    
    public static String generate(String javaFilePath,String serviceUrl,String type) throws IOException{
    	
    	if(!"ajax".equals(type) && !"axios".equals(type)) {
    		
    		return null;
    	}
    	
    	File f=new File(javaFilePath);
    	
    	List<ModuleInfo> modules=SourceParser.parse(f);
    	if(modules==null){
    		return null;
    	}
    	new ParserContext().parse(f);
    	map.put("serviceUrl", serviceUrl);
    	map.put("modules", modules);
    	ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/beetl", "utf-8");
		Configuration cfg = Configuration.defaultConfiguration();
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
		Template t = gt.getTemplate("js-api-"+type+".txt");
		t.binding(map);
		String str = t.render();
		return str;
    	
    }
    
	 

	private static void useBeetl() throws IOException {
		File f=new File("E:\\Code\\STSCode\\hnvmns-auth\\src\\main\\java\\com\\hngd\\web\\controller\\MenuController.java");
    	f=new File("D:\\company\\projects\\inspection-system\\base-data\\src\\main\\java\\com\\hngd\\web\\controller\\PersonnelController.java");
		List<ModuleInfo> modules=SourceParser.parse(f);
    	
		//System.out.println(GsonUtils.toJsonString(modules));
    	map.put("modules", modules);
    	map.put("serviceUrl", "/base-data");
    	ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/beetl", "utf-8");
		Configuration cfg = Configuration.defaultConfiguration();
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
		Template t = gt.getTemplate("js-api-axios.txt");
		//t.binding("name", "beetl");
		t.binding(map);
		String str = t.render();
		System.out.println(str);
		
	}
	public static void main(String[] args) throws IOException {
		useBeetl();
	}
}
