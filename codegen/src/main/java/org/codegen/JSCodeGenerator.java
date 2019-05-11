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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import com.hngd.doc.core.InterfaceInfo.RequestParameterInfo;
import com.hngd.doc.core.MethodInfo;
import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.parse.CommonClassCommentParser;
import com.hngd.doc.core.parse.ModuleParser;

 
 

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
    	
    	List<ModuleInfo> modules=ModuleParser.parse(f);
    	if(modules==null){
    		return null;
    	}
    	CommonClassCommentParser.parse(f);
    	attachCommentInfo(modules);
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
    
	private static void attachCommentInfo(List<ModuleInfo> modules) {
		for(ModuleInfo mi:modules){
			mi.interfaceInfos.forEach(ii->{
				
				String key=mi.simpleClassName+"#"+ii.methodName;
				MethodInfo mm=CommonClassCommentParser.methodComments.get(key);
				if(mm!=null){
					ii.comment=mm.comment;
					for(int i=0;i<ii.parameterInfos.size();i++){
						RequestParameterInfo pi=ii.parameterInfos.get(i);
						if(i<mm.parameters.size()){
							pi.comment=mm.parameters.get(i).comment;
						}
					}
				}
				
			});
			
			
		}
		
	}

	private static void useBeetl() throws IOException {
		File f=new File("E:\\Code\\STSCode\\hnvmns-auth\\src\\main\\java\\com\\hngd\\web\\controller\\MenuController.java");
    	List<ModuleInfo> modules=ModuleParser.parse(f);
    	
		//System.out.println(GsonUtils.toJsonString(modules));
    	map.put("modules", modules);
    	ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/beetl", "utf-8");
		Configuration cfg = Configuration.defaultConfiguration();
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
		Template t = gt.getTemplate("jscode.txt");
		//t.binding("name", "beetl");
		t.binding(map);
		String str = t.render();
		System.out.println(str);
		
	}
}
