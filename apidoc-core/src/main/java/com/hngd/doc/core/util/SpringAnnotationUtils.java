package com.hngd.doc.core.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

public class SpringAnnotationUtils {

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
}
