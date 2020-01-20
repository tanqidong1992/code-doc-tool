package com.hngd.parser.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.experimental.Accessors;
/**
 * 基础信息
 * @author tqd
 *
 */
@Data
@Accessors(chain = true)
public class BaseInfo {

	/**
	 * 名称
	 */
	private String name;
	/**
	 * 注释
	 */
	private String comment;
  
    public Map<String,String> extensions=new HashMap<>();
     
	public void addExtension(String key,String value) {
		if(StringUtils.isEmpty(key) || value==null) {
			return;
		}
		extensions.put(key, value);
	}
}
