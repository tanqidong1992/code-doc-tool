package com.hngd.doc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "doc")
public class DocumentProperties {
    /**
     * 系统名称
     */
    private String systemName;
    /**
     * 工作目录
     */
    private String workDirectory=".";
}
