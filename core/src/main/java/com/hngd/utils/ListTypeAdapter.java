 package com.hngd.utils;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ListTypeAdapter implements JsonSerializer<List>{

	Gson gson=new Gson();
	@Override
	public JsonElement serialize(List src, Type typeOfSrc, JsonSerializationContext context) {
		// TODO Auto-generated method stub
		if(src==null ||src.size()<=0)
		return null;
		else{
			
			return gson.toJsonTree(src);
		}
	}
	
	
}
