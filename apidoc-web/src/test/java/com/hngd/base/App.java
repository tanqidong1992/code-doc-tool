
package com.hngd.base;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.api.doc.config.ServerConfig;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hngd.doc.core.gen.OpenAPITool;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.doc.core.parse.EntityClassCommentParser;

/**
 * Hello world!
 * scp api.json root@106.12.205.90:/home/apps/apache-tomcat-8.5.38/webapps/apidoc
 */
@SuppressWarnings("deprecation")
public class App {
	
	static {
		PropertyConfigurator.configure("./log4j.properties");
	}
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	static final Charset utf8 = Charset.forName("UTF-8");
	static List<String> application_json = Arrays.asList("application/json", "*");
	static List<String> application_url_encode = Arrays.asList("application/x-www-form-urlencoded");

	public static void resolvePacakge(String packageName, OpenAPI openAPI) {
		String packagePath = packageName.replaceAll("\\.", "/");
		Enumeration<URL> dirs = null;
		try {
			dirs = App.class.getClassLoader().getResources(packagePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			File file = new File(url.getFile());
			// 把此目录下的所有文件列出
			String[] classes = file.list();
			// 循环此数组，并把.class去掉
			for (String className : classes) {
				if(!className.endsWith(".class")) {
					logger.warn("file:{} is not a class file", className);
					continue;
				}
				try {
				className = className.substring(0, className.length() - 6);
				}catch(Exception e) {
					e.printStackTrace();
				}
				// 拼接上包名，变成全限定名
				String qName = packageName + "." + className;
				if (qName.endsWith("Example")) {
					continue;
				}
				// 如有需要，把每个类生实一个实例
				Class<?> cls = null;
				try {
					cls = Class.forName(qName);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				OpenAPITool.resolveType(cls, openAPI);
			}
		}
	}

	public static Info createInfo() {
		Contact contact = new Contact();
		contact.setEmail("903843602@qq.com");
		contact.setName("谭奇栋");
		contact.setUrl("http://192.168.0.239/web");
		License license = new License();
		license.setName("参考资料");
		license.setUrl("http://192.168.0.239/hndoc/");
		Info info = new Info();
		String timeStr = dateFormat(new Date());
		info.setDescription("更新时间:" + timeStr + "");
		info.setContact(contact);
		info.setTermsOfService("api");
		info.setTitle("巡检子系统接口文档");
		info.setVersion("V1.0.1");
		info.setLicense(license);
		return info;
	}

	private static String dateFormat(Date date) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(date);
	}

	public static OpenAPI mOpenAPI;
	/**
	 * entity类源代码所在位置
	 */
	public static final String[] ENTITY_CLASS_SRC_DIC = {
			"D:\\company\\projects\\inspection-system\\inspection-system\\src\\main\\java\\com\\hngd\\model",
			"D:\\company\\projects\\inspection-system\\inspection-system\\src\\main\\java\\com\\hngd\\web\\dto",
			"D:\\company\\projects\\inspection-system\\inspection-system\\src\\main\\java\\com\\hngd\\dto" };
	/**
	 * controller类源代码所在位置
	 */
	public static final String[] CONTROLLER_CLASS_SRC_DIC = {
			"D:\\company\\projects\\inspection-system\\inspection-system\\src\\main\\java\\com\\hngd\\web\\controller"

	};

	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();
		Thread t1 = new Thread(() -> {
			EntityClassCommentParser.init(ENTITY_CLASS_SRC_DIC);
		});
		Thread t2 = new Thread(() -> {
			for (String dir : CONTROLLER_CLASS_SRC_DIC) {
				ControllerClassCommentParser.init(dir);
			}
		});
		t1.start();
		t2.start();
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Info info = createInfo();
		OpenAPI openApi = new OpenAPI();
		openApi.setInfo(info);

		Server serversItem = new Server();
		serversItem.setUrl("https://192.168.0.144:8080/inspection");
		openApi.addServersItem(serversItem);

		//Map<String, Model> definitions = new HashMap<String, Model>();

		resolvePacakge("com.hngd.model", openApi);
		OpenAPITool openAPITool = new OpenAPITool(openApi);
		openAPITool.parse("com.hngd.web.controller");
		mOpenAPI = openApi;
		String s = toJson("");
        FileUtils.write(new File("./api.json"), s);
		//System.out.println(s);
	}

	public static synchronized void init(ServerConfig config)
			throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		long startTime = System.currentTimeMillis();
		Thread t1 = new Thread(() -> {
			EntityClassCommentParser.init(ENTITY_CLASS_SRC_DIC);
		});
		Thread t2 = new Thread(() -> {
			for (String dir : CONTROLLER_CLASS_SRC_DIC) {
				ControllerClassCommentParser.init(dir);
			}
		});
		t1.start();
		t2.start();
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Info info = config.info != null ? config.info : createInfo();
		OpenAPI swagger = new OpenAPI();
		swagger.setInfo(info);

		Server server = new Server();
		server.setUrl("192.168.0.144:8080/inspection");
		List<Server> servers = Arrays.asList(server);
		swagger.setServers(servers);
		// Map<String, Model> definitions = new HashMap<String, Model>();
		Components components = new Components();
		swagger.components(components);
		resolvePacakge("com.hngd.model", swagger);
		// resolvePacakge("com.hngd.entity", swagger);
		// SwaggerDocGenerator.resolveType(PageEntity.class, swagger);
		OpenAPITool sdg = new OpenAPITool(swagger);
		sdg.parse("com.hngd.web.controller");

		/*
		 * for (String dir : CONTROLLER_CLASS_SRC_DIC) { sdg.parse(new File(dir)); }
		 */
		List<Tag> tags = swagger.getTags();
		tags.sort(new Comparator<Tag>() {

			@Override
			public int compare(Tag o1, Tag o2) {
				return o1.getName().compareTo(o2.getName()) * -1;
				// return 0;
			}
		});
		mOpenAPI = swagger;
		logger.info("init using time:" + (System.currentTimeMillis() - startTime) + "ms");
	}



	/**
	 * @param key
	 * @return
	 * @author
	 * @throws JsonProcessingException
	 * @since 1.0.0
	 * @time 2017年2月14日 上午9:16:51
	 */
	public static String toJson(final String key) throws JsonProcessingException {

		// SpecFilter sf = new SpecFilter();
		// OpenAPISpecFilter ssf=new ;
		// Swagger swagger = sf.filter(mSwagger, ssf, null, null, null);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		return mapper.writeValueAsString(mOpenAPI);
	}

	public static String[] getAllSrcDir() {
		int size = ENTITY_CLASS_SRC_DIC.length + CONTROLLER_CLASS_SRC_DIC.length;
		String[] dirs = new String[size];
		System.arraycopy(ENTITY_CLASS_SRC_DIC, 0, dirs, 0, ENTITY_CLASS_SRC_DIC.length);
		System.arraycopy(CONTROLLER_CLASS_SRC_DIC, 0, dirs, ENTITY_CLASS_SRC_DIC.length,
				CONTROLLER_CLASS_SRC_DIC.length);
		return dirs;
	}
}
