/**
 * Copyright (c) 2015,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * 文件名：RestDescriptionGenerator.java
 * 时间：2015年11月30日 下午5:07:36
 * 作者：Administrator
 * 备注：
 */

package org.docgen;

import java.io.File;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
 
import java.lang.reflect.ParameterizedType;
 
import java.lang.reflect.Type;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hngd.doc.core.InterfaceInfo;
import com.hngd.doc.core.MethodInfo;
import com.hngd.doc.core.MethodInfo.ParameterInfo;
import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.gen.SwaggerDocGenerator;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.web.controller.*;
 

/**
 * @author Administrator
 */
public class RestDescriptionGenerator
{
    public static final boolean isRxJavaSupported = true;
    static {
		PropertyConfigurator.configure("./log4j.properties");
	}
    /**
     * controller类源代码所在位置
     */
    public static final String[] CONTROLLER_CLASS_SRC_DIC =
    { "F:\\HNOE_TQD_JAVA\\JavaCode\\HNVMNS6000\\web\\src\\main\\java\\com\\hngd\\web\\controller" };

    private static final String HOST_NAME = "http://192.168.0.156:8080/web";

    public static void main(String[] args) throws ClassNotFoundException
    {
    	ControllerClassCommentParser.init(CONTROLLER_CLASS_SRC_DIC[0]);
        URL url = RestDescriptionGenerator.class.getClassLoader().getResource("com/hngd/web/controller");
        String protocol = url.getProtocol();
        System.out.println(protocol);
        System.out.println(url.getPath());
        File root = new File(url.getPath());
        List<ModuleInfo> infos = new LinkedList<ModuleInfo>();
/*        for (File file : root.listFiles())
        {
            String fileName = file.getName();
            String className = "com.hngd.web.controller." + fileName.split("\\.")[0];
            Class<?> cls = Class.forName(className);
            ModuleInfo mi = processClass(cls);
            if (mi != null)
            {
                infos.add(mi);
            }
            // System.err.println(cls.getName());
        }*/
        
        List<Class<?>> resolvedClasses=Arrays.asList();
        resolvedClasses.forEach(c->{
        	 ModuleInfo mi = SwaggerDocGenerator.processClass(c);
             if (mi != null)
             {
                 infos.add(mi);
             }
        });
        
        
        System.out.println("--------------------------------------");
        for (ModuleInfo mi : infos)
        {
            //System.out.println(mi.toTestCode());
           
            System.out.println("---");
            String classComment=ControllerClassCommentParser.classComments.get(mi.className);
            System.out.println("###模块名称:" + (classComment!=null?classComment:mi.className));
            for (InterfaceInfo ii : mi.interfaceInfos)
            {
            	MethodInfo methodComment=ControllerClassCommentParser.methodComments.get(mi.className+"#"+ii.methodName);
                if(methodComment==null) {
                	continue;
                }
            	System.out.println("####接口名称:" + ii.methodName);
                System.out.println("");
                System.out.println("- 功能:"+methodComment.comment);
                System.out.println("");
                System.out.println(
                        "- 请求方式 " + ii.requestType + " " +mi.moduleUrl+ii.methodUrl);
                if (ii.parameterTypes.size() > 0)
                {
                    System.out.println("- 输入参数列表");
                    System.out.println("");
                    System.out.println("|参数名称|参数类型|备注|");
                    System.out.println("|:--|:--|:--|");
                    if (ii.methodName.equals("login"))
                    {
                        System.out.println("");
                    }
                    
                    for (int i = 0; i < ii.parameterTypes.size(); i++)
                    {
                    	String ic="";
                    	if(i<methodComment.parameters.size()){
                    		ParameterInfo pi=methodComment.parameters.get(i);
                    		ic=pi.comment;
                    	}
                        if (i >= ii.parameterNames.size())
                        {
                            System.out.println("|" + "暂无" + "|" + ii.parameterTypes.get(i).getTypeName() + "|"+ic+"|");
                        } else
                        {
                            String paramaterName = ii.parameterNames.get(i).name;
                            if ("page".equals(paramaterName))
                            {
                                System.out.println("|" + paramaterName + "|" + ii.parameterTypes.get(i).getTypeName()
                                        + "|"+ic+"|");
                            } else
                                System.out.println("|" + paramaterName + "|" + ii.parameterTypes.get(i).getTypeName() + "|"+ic+"|");
                        }
                    }
                } else
                {
                    System.out.println("- 无参数");
                }
                System.out.println("");
                System.out.println("- 返回结果 " + ii.retureType);
                System.out.println("");
            }
            System.out.println("---");
        }
    }

    /**
     * @param moduleName
     * @return
     * @作者:Administrator
     * @时间:2016年1月21日 下午12:04:27
     * @备注:
     */
    public static String firstToUpcase(String moduleName)
    {
        if (moduleName == null || moduleName.length() <= 0)
        {
            return null;
        }
        // TODO Auto-generated method stub
        char c = moduleName.charAt(0);
        c = Character.toUpperCase(c);
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        sb.append(moduleName.substring(1));
        return sb.toString();
    }


}
