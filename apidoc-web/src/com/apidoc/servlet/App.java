
package com.apidoc.servlet;
 
import io.swagger.models.apideclaration.Model;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apidoc.config.ServerConfig;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hngd.doc.core.gen.OpenAPITool;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.doc.core.parse.EntityClassCommentParser;

/**
 * Hello world!
 */
@SuppressWarnings("deprecation")
public class App
{
    private static final Logger logger                 = LoggerFactory.getLogger(App.class);
    static final Charset        utf8                   = Charset.forName("UTF-8");
    static List<String>         application_json       = Arrays.asList("application/json", "*");
    static List<String>         application_url_encode = Arrays.asList("application/x-www-form-urlencoded");

    public static void resolvePacakge(String packageName, OpenAPI swagger)
    {
        String packagePath = packageName.replaceAll("\\.", "/");
        Enumeration<URL> dirs = null;
        try
        {
            dirs = App.class.getClassLoader().getResources(packagePath);
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (dirs.hasMoreElements())
        {
            URL url = dirs.nextElement();
            File file = new File(url.getFile());
            // 把此目录下的所有文件列出
            String[] classes = file.list();
            // 循环此数组，并把.class去掉
            for (String className : classes)
            {
                className = className.substring(0, className.length() - 6);
                // 拼接上包名，变成全限定名
                String qName = packageName + "." + className;
                if (qName.endsWith("Example"))
                {
                    continue;
                }
                // 如有需要，把每个类生实一个实例
                Class<?> cls = null;
                try
                {
                    cls = Class.forName(qName);
                } catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
                OpenAPITool.resolveType(cls, swagger);
            }
        }
    }

    public static Info createInfo()
    {
        Contact contact = new Contact();
        contact.setEmail("903843602@qq.com");
        contact.setName("谭奇栋");
        contact.setUrl("http://192.168.0.156/web");
        License license = new License();
        license.setName("参考资料");
        license.setUrl("http://192.168.0.156/hndoc/");
        Info info = new Info();
        String timeStr = dateFormat(new Date());
        info.setDescription("更新时间:" + timeStr + "");
        info.setContact(contact);
        info.setTermsOfService("api");
        info.setTitle("HNVMNS6000-web接口文档");
        info.setVersion("V1.0.1");
        info.setLicense(license);
        return info;
    }

    private static String dateFormat(Date date)
    {
        // TODO Auto-generated method stub
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(date);
    }
    public static OpenAPI        mSwagger;
    /**
     * entity类源代码所在位置
     */
    public static final String[] ENTITY_CLASS_SRC_DIC     =
    {       "F:\\HNOE_TQD_JAVA\\JavaCode\\HNVMNS6000\\dao\\src\\main\\java\\com\\hngd\\model",
            "F:\\HNOE_TQD_JAVA\\JavaCode\\HNVMNS6000\\dao\\src\\main\\java\\com\\hngd\\entity"};
    /**
     * controller类源代码所在位置
     */
    public static final String[] CONTROLLER_CLASS_SRC_DIC =
    { "F:\\HNOE_TQD_JAVA\\JavaCode\\HNVMNS6000\\web\\src\\main\\java\\com\\hngd\\web\\controller"
    		 
    };

    public static void main(String[] args) throws JsonProcessingException {
		
        long startTime = System.currentTimeMillis();
        Thread t1 = new Thread(() ->
        {
            EntityClassCommentParser.init(ENTITY_CLASS_SRC_DIC);
        });
        Thread t2 = new Thread(() ->
        {
            for (String dir : CONTROLLER_CLASS_SRC_DIC)
            {
                ControllerClassCommentParser.init(dir);
            }
        });
        t1.start();
        t2.start();
        try
        {
            t1.join();
            t2.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
    	Info info =createInfo();
    	OpenAPI openApi = new OpenAPI();
        openApi.setInfo(info);
 
        Server serversItem=new Server();
        serversItem.setUrl("http://192.168.0.156:8080/web/api");
		openApi.addServersItem(serversItem);
		
        Map<String, Model> definitions = new HashMap<String, Model>();
        openApi.
        resolvePacakge("com.hngd.model", openApi);
        // resolvePacakge("com.hngd.entity", swagger);
       // SwaggerDocGenerator.resolveType(PageEntity.class, swagger);
        OpenAPITool sdg = new OpenAPITool(openApi);
        //sdg.parse(Arrays.asList(UserController.class));
        sdg.parse("com.hngd.web.controller");
        mSwagger=openApi;
        String s=toJson("");
        
        System.out.println(s);
	}
    
    public static synchronized void init(ServerConfig config)
            throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        long startTime = System.currentTimeMillis();
        Thread t1 = new Thread(() ->
        {
            EntityClassCommentParser.init(ENTITY_CLASS_SRC_DIC);
        });
        Thread t2 = new Thread(() ->
        {
            for (String dir : CONTROLLER_CLASS_SRC_DIC)
            {
                ControllerClassCommentParser.init(dir);
            }
        });
        t1.start();
        t2.start();
        try
        {
            t1.join();
            t2.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        Info info =config.apiDocInfo!=null?config.apiDocInfo:createInfo();
        Swagger swagger = new Swagger();
        swagger.setInfo(info);
        swagger.setBasePath("/web/api");
        swagger.setHost("192.168.0.156:8080");
        Map<String, Model> definitions = new HashMap<String, Model>();
        swagger.setDefinitions(definitions);
        resolvePacakge("com.hngd.model", swagger);
        // resolvePacakge("com.hngd.entity", swagger);
       // SwaggerDocGenerator.resolveType(PageEntity.class, swagger);
        OpenAPITool sdg = new OpenAPITool(swagger);
        sdg.parse("com.hngd.web.controller");
        
/*        for (String dir : CONTROLLER_CLASS_SRC_DIC)
        {
        	sdg.parse(new File(dir));
        }*/
        List<Tag> tags=swagger.getTags();
        tags.sort(new Comparator<Tag>()
        {

            @Override
            public int compare(Tag o1, Tag o2)
            {
               return o1.getName().compareTo(o2.getName())*-1;
                //return 0;
            }
        });
        mSwagger = swagger;
        logger.info("init using time:" + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static String toJson() throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        return mapper.writeValueAsString(mSwagger);
    }

    /**
     * @param key
     * @return
     * @author
     * @throws JsonProcessingException
     * @since 1.0.0
     * @time 2017年2月14日 上午9:16:51
     */
    public static String toJson(final String key) throws JsonProcessingException
    {
        SwaggerSpecFilter ssf = new SwaggerSpecFilter()
        {
            @Override
            public boolean isPropertyAllowed(Model model, Property property, String propertyName,
                    Map<String, List<String>> params, Map<String, String> cookies, Map<String, List<String>> headers)
            {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public boolean isParamAllowed(Parameter parameter, Operation operation, ApiDescription api,
                    Map<String, List<String>> params, Map<String, String> cookies, Map<String, List<String>> headers)
            {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public boolean isOperationAllowed(Operation operation, ApiDescription api, Map<String, List<String>> params,
                    Map<String, String> cookies, Map<String, List<String>> headers)
            {
                String desc = operation.getDescription();
                String id = operation.getOperationId();
                List<String> tags = operation.getTags();
                String path = api.getPath();
                if (!StringUtils.isEmpty(desc) && desc.contains(key))
                {
                    return true;
                }
                if (!StringUtils.isEmpty(id) && id.contains(key))
                {
                    return true;
                }
                if (!StringUtils.isEmpty(path) && path.contains(key))
                {
                    return true;
                }
                if (tags != null && tags.size() > 0)
                {
                    Optional<String> foundOne = tags
                            .stream()
                            .filter(tag -> !StringUtils.isEmpty(tag) && tag.contains(key))
                            .findFirst();
                    return foundOne.isPresent();
                }
                return false;
            }
        };
        SpecFilter sf = new SpecFilter();
        Swagger swagger = sf.filter(mSwagger, ssf, null, null, null);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        return mapper.writeValueAsString(swagger);
    }

    public static String[] getAllSrcDir()
    {
        int size = ENTITY_CLASS_SRC_DIC.length + CONTROLLER_CLASS_SRC_DIC.length;
        String[] dirs = new String[size];
        System.arraycopy(ENTITY_CLASS_SRC_DIC, 0, dirs, 0, ENTITY_CLASS_SRC_DIC.length);
        System.arraycopy(CONTROLLER_CLASS_SRC_DIC, 0, dirs, ENTITY_CLASS_SRC_DIC.length,
                CONTROLLER_CLASS_SRC_DIC.length);
        return dirs;
    }
}
