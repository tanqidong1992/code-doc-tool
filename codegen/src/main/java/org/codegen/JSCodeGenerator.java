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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hngd.doc.core.gen.SwaggerDocGenerator;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.doc.core.util.TypeUtils;
import com.hngd.web.controller.*;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

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
    public static class ModuleInfo
    {
        String              name;
        String              url;
        List<InterfaceInfo> interfaces = new ArrayList<>();

        public final String getName()
        {
            return name;
        }

        public final void setName(String name)
        {
            this.name = name;
        }

        public final String getUrl()
        {
            return url;
        }

        public final void setUrl(String url)
        {
            this.url = url;
        }

        public final List<InterfaceInfo> getInterfaces()
        {
            return interfaces;
        }

        public final void setInterfaces(List<InterfaceInfo> interfaces)
        {
            this.interfaces = interfaces;
        }
    }

    public static class InterfaceInfo
    {
        boolean multipart;

        public final boolean isMultipart()
        {
            return multipart;
        }

        public final void setMultipart(boolean multipart)
        {
            this.multipart = multipart;
        }

        public final String getReturnType()
        {
            return returnType;
        }

        public final void setReturnType(String returnType)
        {
            this.returnType = returnType;
        }
        String              returnType;
        String              name;
        String              url;
        List<ParameterInfo> parameters = new ArrayList<>();
        String              requestType;
        String comment;

        /**
		 * @return the comment
		 */
		public final String getComment() {
			return comment;
		}

		/**
		 * @param comment the comment to set
		 */
		public final void setComment(String comment) {
			this.comment = comment;
		}

		public final String getName()
        {
            return name;
        }

        public final void setName(String name)
        {
            this.name = name;
        }

        public final String getUrl()
        {
            return url;
        }

        public final void setUrl(String url)
        {
            this.url = url;
        }

        public final List<ParameterInfo> getParameters()
        {
            return parameters;
        }

        public final void setParameters(List<ParameterInfo> parameters)
        {
            this.parameters = parameters;
        }

        public final String getRequestType()
        {
            return requestType;
        }

        public final void setRequestType(String requestType)
        {
            this.requestType = requestType;
        }
    }

    public static class ParameterInfo
    {
        String  name;
        String  type;
        String  comment;
        Boolean isPrimitive;
        Boolean isPathVariable;
        public final Boolean getIsPathVariable()
        {
            return isPathVariable;
        }

        public final void setIsPathVariable(Boolean isPathVariable)
        {
            this.isPathVariable = isPathVariable;
        }

        public final String getName()
        {
            return name;
        }

        public final void setName(String name)
        {
            this.name = name;
        }

        public final String getType()
        {
            return type;
        }

        public final void setType(String type)
        {
            this.type = type;
        }

        public final String getComment()
        {
            return comment;
        }

        public final void setComment(String comment)
        {
            this.comment = comment;
        }

        public final Boolean getIsPrimitive()
        {
            return isPrimitive;
        }

        public final void setIsPrimitive(Boolean isPrimitive)
        {
            this.isPrimitive = isPrimitive;
        }
    }
    static Map<String, List<ModuleInfo>> map = new HashMap<>();
    static
    {
    	 
    	ControllerClassCommentParser.init(CONTROLLER_CLASS_SRC_DIC[0]);
    	cls=SwaggerDocGenerator.getPacakge("com.hngd.web.controller");
        List<ModuleInfo> mis = new LinkedList<>();
        cls.stream().filter(c -> c.getAnnotation(RequestMapping.class) != null).forEach(c ->
        {
        	String className=c.getSimpleName();
            RequestMapping crm = c.getAnnotation(RequestMapping.class);
            ModuleInfo mi = new ModuleInfo();
            mi.url = crm.value()[0];
            mi.name = mi.url.substring(1).contains("/") ? c.getSimpleName() : mi.url.substring(1);
            Arrays
                    .asList(c.getDeclaredMethods())
                    .stream()
                    .filter(m -> m.getAnnotation(RequestMapping.class) != null)
                    .forEach(m ->
                    {
                    	
                        InterfaceInfo ii = new InterfaceInfo();
                        RequestMapping mrm = m.getAnnotation(RequestMapping.class);
                        ii.url = mrm.value()[0];
                        ii.name = m.getName();
                        ii.requestType = mrm.method()[0].name().toLowerCase();
                        String methodName=m.getName();
                        ii.comment=ControllerClassCommentParser.getMethodComment(className, methodName);
                        Arrays
                                .asList(m.getParameters())
                                .stream()
                                .filter(p -> (p.getAnnotation(RequestParam.class) != null
                                        || p.getAnnotation(PathVariable.class)!=null))
                                .forEach(p ->
                                {
                                    ParameterInfo pi = new ParameterInfo();
                                    RequestParam prp = p.getAnnotation(RequestParam.class);
                                    if (prp != null)
                                    {
                                        pi.name = prp.value();
                                        pi.isPathVariable=false;
                                    } else
                                    {
                                        pi.isPathVariable=true;
                                        PathVariable pv = p.getAnnotation(PathVariable.class);
                                        pi.name = pv.value();
                                    }
                                    Type parameterType = p.getParameterizedType();
                                    pi.type = TypeUtils.getTypeName(parameterType);
                                    pi.isPrimitive = TypeUtils.isPrimitiveType(parameterType);
                                    pi.comment=ControllerClassCommentParser.getParameterComment(className, methodName, pi.name);
                                    ii.parameters.add(pi);
                                    if (!ii.multipart)
                                    {
                                        ii.multipart = TypeUtils.isMultipartType(parameterType);
                                    }
                                });
                        Type returnType = m.getGenericReturnType();
                        ii.returnType = TypeUtils.getTypeName(returnType);
                        String rt = SPRING2RETROFIT.get(ii.returnType);
                        if (rt != null)
                        {
                            ii.returnType = rt;
                        }
                        mi.interfaces.add(ii);
                    });
            mis.add(mi);
        });
        map.put("modules", mis);
    }

    public static void main(String[] args) throws IOException
    {
        File templateFile = new File("templates/jscode.txt");
       // ControllerClassParser.init();
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        File dir = new File("temp");
        cfg.setDirectoryForTemplateLoading(dir);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        Template t = new Template("jsCOdes", new FileReader(templateFile), cfg);
//        FileOutputStream fos = FileUtils.openOutputStream(
  //              new File("E:\\HNOE_TQD_DOC\\HN\\web-test\\src\\main\\java\\com\\hngd\\retrofit\\WebInterface.java"));
        String filePath="F:\\HNOE_TQD_DOC\\HN\\HNFrontToEnd\\common\\js\\client.js";
         FileOutputStream fos=FileUtils.openOutputStream(new File(filePath));
        Writer out = new OutputStreamWriter(fos);
        try
        {
            t.process(map, out);
        } catch (TemplateException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out.flush();
        out.close();
    }
}
