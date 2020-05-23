package com.hngd.doc.entity;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.Data;
/**
 * 接口文档信息
 * @author tqd
 *
 */
@Data
public class DocumentInfo {

	/**
	 * 原始文件名称
	 */
	private String filename;
	/**
	 * OpenAPI对象
	 */
	private OpenAPI openAPI;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 版本
	 */
	private String version;
	/**
	 * 最后更新时间
	 */
	private Long lastUpdateTime;
	/**
	 * 子模块
	 */
	private List<MyTag> tags;
 
	public static class MyTag{
		public String name;
		public String query;
		public static MyTag fromTag(String filename,Tag tag) {
			MyTag mt=new MyTag();
			mt.query= filename+"/"+tag.getName();
			mt.name=tag.getName();
			
			return mt;
		}
	}
	
	
}
