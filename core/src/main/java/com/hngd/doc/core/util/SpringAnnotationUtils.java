package com.hngd.doc.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class SpringAnnotationUtils {

	private static final Logger logger=LoggerFactory.getLogger(SpringAnnotationUtils.class);
	public static String HTTP_PATH_DELIMITER="/";
	/**
	 * 从RequestMapping中提取出请求url，req为null则返回"/"
	 * @param req，
	 * @return
	 */
	public static String extractUrl(RequestMapping req) {
		
		if(req==null) {
			return HTTP_PATH_DELIMITER;
		}
		String[] values=req.value();
		String[] paths=req.path();
		String url=null;
		if(paths!=null && paths.length>0) {
			url=paths[0];
		}else if(values!=null && values.length>0) {
			url=values[0];
		}
		if(url==null) {
		    return HTTP_PATH_DELIMITER;
		}else {
			if (!StringUtils.startsWith(url, HTTP_PATH_DELIMITER)) {
				url = HTTP_PATH_DELIMITER + url;
			}
			return url;
		}
		
	}
	
	public static boolean isControllerClass(Class<?> cls) {
		//test class can initialized
		
		try {
			ClassLoader loader=cls.getClassLoader();
			Class.forName(cls.getName(), false,loader);
		} catch (Throwable e) {
			logger.warn("load class failed",e);
			//e.printStackTrace();
			return false;
		}
		
		Controller controller = cls.getAnnotation(Controller.class);
		RestController restController = cls.getAnnotation(RestController.class);
		if (controller == null && restController == null) {
			logger.warn("There is no annotation Controller RestController for class:{}",cls.getName());
			return false;
		}
		return true;
		
	}
}
