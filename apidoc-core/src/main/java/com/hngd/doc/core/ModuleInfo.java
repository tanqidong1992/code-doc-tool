package com.hngd.doc.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hnoe-dev-tqd
 *
 */
public class ModuleInfo {

	public ModuleInfo() {
		interfaceInfos = new ArrayList<InterfaceInfo>();
	}

	public String moduleName;
	public String moduleUrl;
	public List<InterfaceInfo> interfaceInfos;
	public String simpleClassName;
	public String canonicalClassName;
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
