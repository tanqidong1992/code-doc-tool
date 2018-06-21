package com.hngd.doc.core;

import java.util.Date;
import java.util.List;

public class MethodInfo {

	public String methodName;
	public String comment;
	public String retComment;
	public List<ParameterInfo> parameters;
	public String createTimeStr;
	public Date createTime;
	public static class ParameterInfo{
		
		public String parameterName;
		public String comment;
		public String ref;
		public boolean isCollection;
		public String type;
		public String format;
		public boolean isArgumentTypePrimitive;
		
	}
}



