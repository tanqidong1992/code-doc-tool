package com.hngd.s2m.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.s2m.constant.Constants;
import com.hngd.s2m.entity.SchemaTable;
import com.hngd.s2m.exception.GenerateException;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

public class SchemaResolver {
	
	public static SchemaResolver newSchemaResolver(OpenAPI openAPI) {
		return new SchemaResolver(openAPI);
	}
	private Map<String,Integer> resolvedSchemas=new HashMap<>();
	private OpenAPI openAPI;
	private SchemaResolver(OpenAPI openAPI) {
		this.openAPI=openAPI;
	}
	private static final Logger logger=LoggerFactory.getLogger(SchemaResolver.class);
    public  SchemaTable doSchemaToTableCells(String tableName,Schema<?> schema){
	    	if(schema==null) {
	    		return null;
	    	}
	    	SchemaTable sti=new SchemaTable();
	    	sti.tableName=tableName;
	    	Map<String, Schema> map=schema.getProperties();
	    	if(map==null) {
	    		return new SchemaTable();
	    	}
	    	sti.tables=map.entrySet().stream()
	    	    .map((e)->{
	    	    	Optional<SchemaTable> optionalSchemaTableInfo=resolveProperty(e);
	    	    	optionalSchemaTableInfo.ifPresent(sti.subTables::add);
	    	    	String refTable=optionalSchemaTableInfo.isPresent()?optionalSchemaTableInfo.get().tableName:null;
	    		return schemaPropertyToCells(e.getKey(),e.getValue(),refTable);
	    	})
	    	  .filter(ts->ts!=null)
	    	 .collect(Collectors.toList());
	    	
	    	return sti;
	    	
	    }
	 
    public List<String> schemaPropertyToCells(String key,Schema<?> ss,String refTable){
    	List<String> s=new ArrayList<>();
		s.add(key);
		String typeStr=ss.getType();
		String format=ss.getFormat();
		if(format!=null) {
			typeStr+="("+format+")";
		}
		s.add(typeStr);
		if(ss.getRequired()!=null) {
		    s.add(ss.getRequired().contains(key)?"是":"否");
		}else {
			s.add("否");
		}
		s.add("暂无");
		s.add("缺失");
		String desc=ss.getDescription();
		if(refTable!=null) {
			if(desc!=null) {
			    desc+=",具体字段参考表:"+refTable;
			}else {
				desc="字段参考表:"+refTable;
			}
		}
		s.add(desc);
		return s;
    }
     public Optional<SchemaTable> rootSchemaToTableCells(Schema<?> schema){
	     return schemaToTableCells(null, schema);
     }
    public  Optional<SchemaTable> schemaToTableCells(String tableName,Schema<?> schema) {
        SchemaTable st=null;
    	try {
    	    if(schema instanceof ArraySchema) {
        	    st=doArraySchemaToTableCells(tableName,(ArraySchema)schema);
    	    }else {
    		    st=doSchemaToTableCells(tableName, schema);
    	     }
    	 }catch(Throwable e) {
 		    throw new GenerateException("解析成表格"+tableName+"失败",e);
 		}
    	return Optional.ofNullable(st);
		
	}
   
    public Optional<SchemaTable> resolveProperty(Entry<String,Schema> schemaNameValuePair) {
    	String keyName=schemaNameValuePair.getKey();
    	Schema<?> schema=schemaNameValuePair.getValue();
    	if("object".equals(schema.getType())) {
    		return schemaToTableCells(keyName,schema);
    	}else if(schema instanceof ArraySchema) {
    		ArraySchema as=(ArraySchema)schema;
    		Schema<?> itemSchema=as.getItems();
    		if(itemSchema.get$ref()!=null) {
    			String refKey=OpenAPIUtils.refToKey(itemSchema.get$ref());
    			if(shouldResolve(refKey)) {
    			    itemSchema=openAPI.getComponents().getSchemas().get(refKey);
    			}
    		}
    		if(itemSchema!=null) {
        		return schemaToTableCells(keyName, itemSchema);
    		}else {
    			logger.error("The item schema of array {} is null",keyName);
    		}
    		
    	}else {
    		if(schema.get$ref()!=null) {
    			String refKey=OpenAPIUtils.refToKey(schema.get$ref());
    			if(shouldResolve(refKey)) {
    				schema=openAPI.getComponents().getSchemas().get(refKey);
        			if(schema!=null) {
        				return schemaToTableCells(keyName,schema);
        			}
    			}
    		}
    	}
    	return Optional.empty();
    }
    
    private boolean shouldResolve(String refKey) {
    	Integer value=resolvedSchemas.get(refKey);
    	if(value==null) {
    		resolvedSchemas.put(refKey, 1);
    	}else if(value>=2){
    		return false;
    	}else {
    		resolvedSchemas.put(refKey, 2);
    	}
    	return true;
    }
}
