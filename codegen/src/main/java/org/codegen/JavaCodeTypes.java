package org.codegen;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import retrofit2.Call;
import rx.Observable;

public class JavaCodeTypes {

	public static final String SYNC="sync";
	public static final String ASYNC="async";
	static Map<String,Class<?>> type2Clazz= new HashMap<>();
	static {
		type2Clazz.put(SYNC, Call.class);
		type2Clazz.put(ASYNC, Observable.class);
	}
	
	public static Class<?> getReturnType(String invokeType){
		
		if(StringUtils.isEmpty(invokeType)) {
			return type2Clazz.get(SYNC);
		}else {
			return type2Clazz.get(invokeType);
		}
	}
	
}
