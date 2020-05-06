package com.hngd.s2m.entity;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

public class OperationWrapper {

	public String path;
	public String method;
	public Operation operation;
	  
	public OperationWrapper(String path,String method, Operation operation) {
		this.method = method;
		this.operation = operation;
		this.path=path;
	}

	public static List<OperationWrapper> parseFromPathItem(String url,PathItem path){
		List<OperationWrapper> ops=new ArrayList<>();
		Operation o=path.getGet();
		if(o!=null) {
			ops.add(new OperationWrapper(url,"GET", o));
		}
		o=path.getPost();
		if(o!=null) {
			ops.add(new OperationWrapper(url,"POST", o));
		}
		o=path.getPut();
		if(o!=null) {
			ops.add(new OperationWrapper(url,"PUT", o));
		}
		o=path.getDelete();
		if(o!=null) {
			ops.add(new OperationWrapper(url,"DELETE", o));
		}
		o=path.getHead();
		if(o!=null) {
			ops.add(new OperationWrapper(url,"HEAD", o));
		}
		o=path.getOptions();
		if(o!=null) {
			ops.add(new OperationWrapper(url,"OPTIONS", o));
		}
		o=path.getPatch();
		if(o!=null) {
			ops.add(new OperationWrapper(url,"PATCH", o));
		}
		o=path.getTrace();
		if(o!=null) {
			ops.add(new OperationWrapper(url,"TRACE", o));
		}
		return ops;
	}
}
