package com.hngd.constant;

import java.nio.charset.Charset;

import org.springframework.http.MediaType;

public class Constants {
    /**
     * 默认消费内容类型
     */
	public static final String DEFAULT_CONSUME_TYPE=MediaType.APPLICATION_JSON_VALUE;
    /**
     * 默认生成内容类型
     */
	public static final String DEFAULT_PRODUCE_TYPE=MediaType.APPLICATION_JSON_VALUE;
	
	public static final String MULTIPART_FILE_TYPE="application/octet-stream";
	public static final String APPLICATION_FORM_URLENCODED_VALUE=MediaType.APPLICATION_FORM_URLENCODED_VALUE;
	public static final String MULTIPART_FORM_DATA="multipart/form-data";
	
	
	public static final String DEFAULT_CHARSET_NAME="utf-8";
	public static final Charset DEFAULT_CHARSET=Charset.forName(DEFAULT_CHARSET_NAME);
}
