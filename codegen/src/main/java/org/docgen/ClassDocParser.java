
package org.docgen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.hngd.dao.DeviceStateViewMapper;
import com.hngd.dao.DictDetailMapper;
import com.hngd.dao.DictDetailViewMapper;
import com.hngd.dao.DictMapper;
import com.hngd.dao.IcmpChangeMapper;
import com.hngd.dao.StatePlanDetailMapper;
import com.hngd.dao.StatePlanMapper;
import com.hngd.dao.StatePlanViewMapper;
import com.hngd.doc.core.parse.ClassCommentParser;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.service.IDeviceStatePlanService;
import com.hngd.service.IDeviceStateService;
import com.hngd.util.GsonUtils;
import com.hngd.web.controller.DeviceStateController;
import com.hngd.web.controller.DeviceStatePlanController;

import japa.parser.ParseException;
 

public class ClassDocParser
{
    static class DClassInfo
    {
        String                   className;
        String                   classDesc;
        Map<String, DMethodInfo> methods = new HashMap<String, DMethodInfo>();

        @Override
        public String toString()
        {
            // TODO Auto-generated method stub
            return GsonUtils.toJsonString(this);
        }
    }
    static Pattern             pattern = Pattern.compile("\\{[^\\{\\})]*\\}");
    static Map<String, String> maps    = new HashMap<>();

    static
    {
        maps.put("selectByExample", "根据条件查询{entityName}信息");
        maps.put("updateByExample", "根据条件修改{entityName}信息");
        maps.put("selectByPrimaryKey", "根据{entityName}Id查询{entityName}信息");
        maps.put("countByExample", "根据条件统计{entityName}记录条数");
        maps.put("insertSelective", "插入{entityName}信息");
        maps.put("deleteByExample", "根据条件删除{entityName}信息");
        maps.put("updateByExampleSelective", "根据条件修改{entityName}信息");
        maps.put("updateByPrimaryKeySelective", "根据{entityName}Id修改{entityName}信息");
        maps.put("updateByPrimaryKey", "根据{entityName}Id修改{entityName}信息");
        maps.put("insert", "插入{entityName}信息");
        maps.put("deleteByPrimaryKey", "根据{entityName}Id删除{entityName}信息");
    }
    static Map<String, String> entityNames = new HashMap<>();

    static
    {
        entityNames.put("AlarmInfoMapper", "告警");
        entityNames.put("AlarmInfoViewMapper", "告警详情");
        entityNames.put("AlarmLevelMapper", "告警等级");
        entityNames.put("AlarmProcessMapper", "告警处理");
        entityNames.put("AlarmRuleDetailViewMapper", "告警规则详情");
        entityNames.put("AlarmRuleKPIDetailMapper", "告警规则");
        entityNames.put("AreaMapper", "地区");
        entityNames.put("CameraInfoMapper", "摄像机");
        entityNames.put("CameraInfoViewMapper", "摄像机详情");
        entityNames.put("DictDetailMapper", "字典项");
        entityNames.put("DictMapper", "字典项大类");
        entityNames.put("EncodeDeviceInfoMapper", "编码器");
        entityNames.put("EncodeDeviceInfoViewMapper", "编码器详情");
        entityNames.put("LinkageDetailInfoViewMapper", "联动配置关联详情");
        entityNames.put("LinkageInfoViewMapper", "联动配置详情");
        entityNames.put("LinkagePlanDetailMapper", "联动配置用户关联");
        entityNames.put("LinkagePlanMapper", "联动配置基本");
        entityNames.put("LogInfoMapper", "日志");
        entityNames.put("LogInfoViewMapper", "日志详情");
        entityNames.put("LoginRestrictionMapper", "登录限制");
        entityNames.put("MenuPermissionMapper", "菜单");
        entityNames.put("OidDictMapper", "告警项");
        entityNames.put("OrganizationMapper", "组织");
        entityNames.put("ResourceBaseInfoMapper", "资源");
        entityNames.put("ResourceTypeMapper", "资源类型");
        entityNames.put("ResourceTypeRelationMapper", "资源类型关联");
        entityNames.put("RoleMapper", "角色");
        entityNames.put("RolePermissionMapper", "角色权限关联");
        entityNames.put("RoleResourceOrgMapper", "角色资源关联");
        entityNames.put("ServiceInfoViewMapper", "服务");
        entityNames.put("TaskInfoViewMapper", "视频质量诊断任务");
        entityNames.put("TimeScheduleMapper", "时间配置");
        entityNames.put("UserInfoMapper", "用户");
        entityNames.put("UserInfoViewMapper", "用户详情");
        entityNames.put("VQDPlanDetailMapper", "任务计划设备关联");
        entityNames.put("VQDPlanMapper", "任务计划基本");
        entityNames.put("VQDPlanTimeMapper", "任务计划时间配置");
        entityNames.put("VQDProcessedResultMapper", "视频质量诊断结果");
        entityNames.put("VQDResultMapper", "视频质量诊断结果原始结果");
        entityNames.put("VQDThresholdRuleMapper", "视频质量诊断阈值配置");
        entityNames.put("WorkOrderInfoMapper", "工单");
        entityNames.put("WorkOrderInfoViewMapper", "工单详情");
        entityNames.put("WorkOrderLogMapper", "工单日志");
        entityNames.put("WorkorderStatBasicMapper", "");
        entityNames.put("WorkorderStatEffiMapper", "");
        entityNames.put("DeviceStateViewMapper", "设备在线状态");
        entityNames.put("IcmpChangeMapper", "设备在线状态记录");
        
        entityNames.put("StatePlanMapper", "设备在线状态巡检计划");
        entityNames.put("StatePlanDetailMapper", "设备在线状态巡检计划目标");
        entityNames.put("StatePlanViewMapper", "设备在线状态巡检计划详细");
        entityNames.put("CameraReferencePictureMapper", "摄像机基准图片");
        
        
        entityNames.put("VqdPtzResultMapper", "设备ptz诊断结果原始");
        entityNames.put("VqdPtzProcessedResultMapper", "设备ptz诊断结果");
        entityNames.put("VqdPtzProcessedLatestResultViewMapper", "设备最新ptz诊断结果详细");
        entityNames.put("VqdPtzProcessedResultViewMapper", "设备ptz诊断结果详细");
        entityNames.put("DictDetailViewMapper", "字典项详细");
        
    }

    static class DFieldInfo
    {
        String     name;
        String     typeName;
        String     initValue;
        public int modifiers;
        String     enclosingClass;
        String     comment;
        int order;
         

        public static String toSimpleHeader()
        {
            String ss = "|序号|成员变量|数据类型|初始值|访问权限|\n" + "|:--|:--|:--|:--|:--|";
            return ss;
        }

        public String toSimpleData()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("|");
            sb.append(order);
            sb.append("|");
            sb.append(name);
            sb.append("|");
            sb.append(typeName);
            sb.append("|");
            sb.append(typeName + "对象");
            sb.append("|");
            if (Modifier.isPrivate(modifiers))
            {
                sb.append("私有");
            } else if (Modifier.isProtected(modifiers))
            {
                sb.append("保护");
            } else if (Modifier.isPublic(modifiers))
            {
                sb.append("公开");
            } else
            {
                sb.append("包内访问");
            }
            sb.append("|");
            return sb.toString().replaceAll("<", "&lt;");
        }
    }

    static class DMethodInfo
    {
        
        String                      name;
        String                      comment;
        Map<String, DParameterInfo> parameters = new HashMap<String, DParameterInfo>();
        String                      retComment;
        String                      retType;
        public int                  modifiers;
        String                      enclosingClass;
        int order;

        @Override
        public String toString()
        {
            return GsonUtils.toJsonString(this);
        }

        public String toSimpleData()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("|");
            sb.append(order);
            sb.append("|");
            sb.append(name);
            sb.append("|");
            sb.append(retType);
            sb.append("|");
            parameters.values().forEach(p ->
            {
                sb.append(p.type);
                sb.append(",");
                // sb.append(p.name+"("+p.type+"),");
            });
            if (sb.charAt(sb.length() - 1) == ',')
            {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("|");
            if (Modifier.isPrivate(modifiers))
            {
                sb.append("私有");
            } else if (Modifier.isProtected(modifiers))
            {
                sb.append("保护");
            } else if (Modifier.isPublic(modifiers))
            {
                sb.append("公开");
            } else
            {
                sb.append("包内访问");
            }
            sb.append("|");
           
                sb.append(comment);
             
            sb.append("|");
            return sb.toString().replaceAll("<", "&lt;");
        }

        public static String toSimpleHeader()
        {
            return "|序号|方法名称|返回类型|参数|访问权限|功能|\n" + "|:--|:--|:--|:--|:--|:--|";
        }
    }

    static class DParameterInfo
    {
        String name;
        String type;
        String comment;

        @Override
        public String toString()
        {
            return GsonUtils.toJsonString(this);
        }
    }
    static int                             i              = 1;
    public static Map<String, DMethodInfo> methodComments = new HashMap<String, DMethodInfo>();
    static String                          controllerRoot = "F:\\HNOE_TQD_JAVA\\JavaCode\\HNVMNS6000\\web\\src\\main\\java\\com\\hngd\\web\\controller";
    static String                          daoRoot        = "F:\\HNOE_TQD_JAVA\\JavaCode\\HNVMNS6000\\dao\\src\\main\\java\\com\\hngd\\dao";
    static String                          serviceRoot    = "F:\\HNOE_TQD_JAVA\\JavaCode\\HNVMNS6000\\service\\src\\main\\java\\com\\hngd\\service";

    public static void main(String[] args) throws ParseException, IOException
    {
    	//ControllerClassCommentParser.init(controllerRoot);
    	//ControllerClassCommentParser.init(daoRoot);
    	 

        List<Class<?>> daos = Arrays.asList(DictMapper.class,DictDetailMapper.class,DictDetailViewMapper.class);
        List<Class<?>> services = Arrays.asList();
        List<Class<?>> controllers = Arrays.asList();
        daos.forEach(c->{
        	File f=new File(daoRoot+File.separator+c.getSimpleName()+".java");
    		try {
				ControllerClassCommentParser.parse(f);
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
        });
       parseDao(daos);
       
       services.forEach(c->{
       	File f=new File(serviceRoot+File.separator+c.getSimpleName()+".java");
   		try {
				ControllerClassCommentParser.parse(f);
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
       });
      parseDao(services);
      
      controllers.forEach(c->{
         	File f=new File(controllerRoot+File.separator+c.getSimpleName()+".java");
     		try {
  				ControllerClassCommentParser.parse(f);
  			} catch (ParseException | IOException e) {
  				e.printStackTrace();
  			}
         });
        parseDao(controllers);
    }

     

    
    public static void parseDao(List<Class<?>> daos)
    {
       
        daos.stream().forEach(clazz ->
        {
        	DClassInfo ci=parseClass(clazz);
            System.out.println("###" + ci.className);
            System.out.println(DMethodInfo.toSimpleHeader());
            ci.methods.values().stream()
            .sorted((m1,m2)->{
            	if(m1.order>m2.order){
            		return 1;
            	}else{
            		return -1;
            	}
            })
            .forEach(m ->
            {
                System.out.println(m.toSimpleData().replaceAll("包内访问", "公开"));
            });
        });
    }
 
	private static DClassInfo parseClass(Class<?> clazz) {
		DClassInfo ci=new DClassInfo();
		ci.className=clazz.getSimpleName();
		Method[] methods=clazz.getDeclaredMethods();
		Field[] fields=clazz.getDeclaredFields();
		int order=0;
		for(int i=0;i<methods.length;i++){
			
			Method method=methods[i];
			int modifiers=method.getModifiers();
			if(Modifier.isStatic(modifiers)){
				continue;
			}
			parseMethod(ci,method,order);
			order++;
			
		}
	 
		return ci;
	}
 
	private static void parseMethod(DClassInfo ci, Method m, int order) {
		 
		String name=m.getName();
		DMethodInfo mi=new DMethodInfo();
		mi.name=name;
		mi.modifiers=m.getModifiers();
		mi.retType=m.getReturnType().getSimpleName();
		mi.order=order;
		mi.enclosingClass=ci.className;
	  
		
		 String comment=maps.get(m.getName());
		 String entityName = entityNames.get(mi.enclosingClass);
         if (comment!=null && entityName != null)
         {
             comment = pattern.matcher(comment).replaceAll(entityName);
            
         }
         
		if(comment==null){
			comment=ControllerClassCommentParser.getMethodComment(ci.className, m.getName());
		} 
		mi.comment=comment;
		 
		Parameter[]  parameters=	m.getParameters();
		if(parameters!=null){
			
			for(int i=0;i<parameters.length;i++){
				Parameter p=parameters[i];
				DParameterInfo pi=new DParameterInfo();
				pi.name=p.getName();
				pi.type=p.getType().getSimpleName();
				 
				String pc=ControllerClassCommentParser.getParameterComment(ci.className, m.getName(), pi.name);
				pi.comment=pc;
				mi.parameters.put(pi.name, pi);
				
			}
			
		}
		 
		ci.methods.put(m.getName(), mi);
		 
	}
 
}
