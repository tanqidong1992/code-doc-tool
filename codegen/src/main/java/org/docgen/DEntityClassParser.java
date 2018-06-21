
package org.docgen;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.hngd.entity.DeviceOnlineHistory;
import com.hngd.entity.DeviceStateStatistic;
import com.hngd.entity.ServiceStatusComposition;
import com.hngd.entity.StatePlanTarget;
import com.hngd.entity.*;

import com.hngd.service.impl.*;
import com.hngd.util.Constant;
import com.hngd.web.controller.DeviceStatePlanController;

public class DEntityClassParser
{
    public static void main(String[] args)
    {
    	List<Class<?>> list=Arrays.asList(
    		 
    		 
    			 
     
    	);
    	list.forEach(c->{
    		parser(c);
    	});
        
    }

    public static void parser(Class<?> cls)
    {
        Field fields[] = cls.getDeclaredFields();
        System.out.println("###" + cls.getSimpleName());
        System.out.println("####属性");
        String ss = "|序号|成员变量|数据类型|初始值|访问权限|\n" + "|:--|:--|:--|:--|:--|";
        System.out.println(ss);
        for (int i = 0; i < fields.length; i++)
        {
            Field f = fields[i];
            StringBuilder sb = new StringBuilder();
            sb.append("|");
            sb.append(i + 1);
            sb.append("|");
            sb.append(f.getName());
            sb.append("|");
            sb.append(f.getType().getSimpleName());
            sb.append("|");
            sb.append("null");
            sb.append("|");
            sb.append("私有");
            sb.append("|");
            System.out.println(sb.toString());
        }
        System.out.println("####方法");
        String ss1 = "|序号|方法名称|类型|参数|访问权限|功能|\n" + "|:--|:--|:--|:--|:--|";
        System.out.println(ss1);
        System.out.println("|无|||||");
    }
}
