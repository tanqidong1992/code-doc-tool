package com.hngd.openapi.constant;

import java.nio.charset.Charset;

public class Constant {

	/**
	 * 默认编码名称
	 */
	public static final String DEFAULT_CHARSET_NAME="utf-8";
	/**
	 * 默认编码
	 */
	public static final Charset DEFAULT_CHARSET=Charset.forName(DEFAULT_CHARSET_NAME);
	
	/**
	 * 最大的文件20M
	 */
	public static final int MAX_OPENAPI_FILE_SIZE=1024*1024*20;
}
