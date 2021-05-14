package com.hngd.doc.constants;

import java.nio.charset.StandardCharsets;

public class Constants {
    /**
     * 上传文件大小限制100M
     */
    public static final int MAX_UPLOAD_FILE_SIZE=100*1024*1024;
    
    /**
     * 默认编码
     */
    public static final String DEFAULT_CHARSET_NAME=StandardCharsets.UTF_8.name();
    
    public static final String TEMP_DIRECTORY_PREFIX="openui-temp-";
}
