package com.hngd.parser.entity;

import java.util.ArrayList;
import java.util.List;

import com.hngd.openapi.entity.HttpInterface;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 模块信息
 * @author hnoe-dev-tqd
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ModuleInfo extends BaseInfo{
 
	/**
	 * 模块基础路径
	 */
	private String url;
	/**
	 * 模块接口列表
	 */
	private List<HttpInterface> interfaceInfos=new ArrayList<HttpInterface>();
	/**
	 * 模块类名称
	 */
	private String simpleClassName;
	/**
	 * 模块所在类全路径
	 */
	private String canonicalClassName;
	/**
	 * 是否被弃用
	 */
	private Boolean deprecated;
    /**
     * 作者
     */
	private String author;
	/**
	 * since
	 */
	private String since;
	 
}
