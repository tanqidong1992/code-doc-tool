package com.hngd.doc.core;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.models.media.Schema;

public class MethodInfo {

	public String methodName;
	public String comment;
	public String retComment;
	public List<ParameterInfo> parameters;
	public String createTimeStr;
	public Date createTime;
 
}



