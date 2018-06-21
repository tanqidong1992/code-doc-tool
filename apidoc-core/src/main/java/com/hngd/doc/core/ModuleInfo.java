package com.hngd.doc.core;

import java.util.ArrayList;
import java.util.List;

public class ModuleInfo {

	public ModuleInfo() {
		interfaceInfos = new ArrayList<InterfaceInfo>();
	}

	public String moduleName;
	public String moduleUrl;
	public List<InterfaceInfo> interfaceInfos;
	public String className;
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
	public final List<InterfaceInfo> getInterfaceInfos() {
		return interfaceInfos;
	}
	/**
	 * @param interfaceInfos the interfaceInfos to set
	 */
	public final void setInterfaceInfos(List<InterfaceInfo> interfaceInfos) {
		this.interfaceInfos = interfaceInfos;
	}
	/**
	 * @return the className
	 */
	public final String getClassName() {
		return className;
	}
	/**
	 * @param className the className to set
	 */
	public final void setClassName(String className) {
		this.className = className;
	}
	
	

}
