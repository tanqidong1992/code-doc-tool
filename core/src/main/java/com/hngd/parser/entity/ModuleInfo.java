package com.hngd.parser.entity;

import java.util.ArrayList;
import java.util.List;

import com.hngd.openapi.entity.HttpInterface;

/**
 * 类信息
 * @author hnoe-dev-tqd
 *
 */
public class ModuleInfo {


    /**
     * 模块名称
     */
	public String moduleName;
	/**
	 * 模块基础路径
	 */
	public String moduleUrl;
	/**
	 * 模块接口列表
	 */
	public List<HttpInterface> interfaceInfos;
	/**
	 * 模块类名称
	 */
	public String simpleClassName;
	/**
	 * 模块所在类全路径
	 */
	public String canonicalClassName;
	/**
	 * 模块是否被弃用
	 */
	public Boolean deprecated;
	public ModuleInfo() {
		interfaceInfos = new ArrayList<HttpInterface>();
	}
	/**
	 * @return the moduleName
	 */
	public final String getModuleName() {
		return moduleName;
	}
	/**
	 * @param moduleName the moduleName to set
	 */
	public final void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	/**
	 * @return the moduleUrl
	 */
	public final String getModuleUrl() {
		return moduleUrl;
	}
	/**
	 * @param moduleUrl the moduleUrl to set
	 */
	public final void setModuleUrl(String moduleUrl) {
		this.moduleUrl = moduleUrl;
	}
	/**
	 * @return the interfaceInfos
	 */
	public final List<HttpInterface> getInterfaceInfos() {
		return interfaceInfos;
	}
	/**
	 * @param interfaceInfos the interfaceInfos to set
	 */
	public final void setInterfaceInfos(List<HttpInterface> interfaceInfos) {
		this.interfaceInfos = interfaceInfos;
	}
	public String getSimpleClassName() {
		return simpleClassName;
	}
	public void setSimpleClassName(String simpleClassName) {
		this.simpleClassName = simpleClassName;
	}
	public String getCanonicalClassName() {
		return canonicalClassName;
	}
	public void setCanonicalClassName(String canonicalClassName) {
		this.canonicalClassName = canonicalClassName;
	}
	 

}
