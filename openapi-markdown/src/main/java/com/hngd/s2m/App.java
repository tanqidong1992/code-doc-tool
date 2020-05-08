package com.hngd.s2m;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.hngd.s2m.entity.OperationWrapper;
import com.hngd.s2m.utils.OpenAPIUtils;

import io.github.swagger2markup.markup.builder.MarkupBlockStyle;
import io.github.swagger2markup.markup.builder.MarkupDocBuilder;
import io.github.swagger2markup.markup.builder.MarkupDocBuilders;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import io.github.swagger2markup.markup.builder.MarkupTableColumn;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final Logger logger=LoggerFactory.getLogger(App.class);
	
    public static void main( String[] args ) throws URISyntaxException, IOException
    {
        File file=new File("./test-data/api.json");
        OpenAPI openAPI=OpenAPIUtils.loadFromFile(file);
       
        
        MarkupDocBuilder builder = MarkupDocBuilders.documentBuilder(MarkupLanguage.MARKDOWN);
        Paths paths=openAPI.getPaths();
        Map<String,List<OperationWrapper>> moduleOps=new HashMap<>();
        paths.forEach((key,pathItem)->{
        	List<OperationWrapper> ops=convertPath(builder,openAPI,key,pathItem);
        	if(!CollectionUtils.isEmpty(ops)) {
        		ops.forEach(op->{
        			List<String> tags=op.operation.getTags();
        			for(String tag:tags) {
        				List<OperationWrapper> tagOps=moduleOps.get(tag);
        				if(tagOps==null) {
        					tagOps=new ArrayList<>();
        					moduleOps.put(tag, tagOps);
        				}
        				tagOps.add(op);
        			}
        		});
        	}
        });
        Info info=openAPI.getInfo();
        builder.documentTitle(info.getTitle());
        builder.textLine(info.getDescription());
        moduleOps.forEach((tag,ops)->{
        	if(!tag.contains("点检") && !tag.contains("月计划开动台时")) {
        		return;
        	}
        	builder.sectionTitleLevel(2, tag);
        	if(!CollectionUtils.isEmpty(ops)) {
        		ops.forEach(op->resolveOperation(op,builder,openAPI));
        	}
        });
        
        
        File output=new File("test-output",info.getTitle()+".md");
        if(output.exists()) {
        	output.delete();
        }
        output=output.getAbsoluteFile();
		builder.writeToFileWithoutExtension(output.toPath(), StandardCharsets.UTF_8,StandardOpenOption.CREATE_NEW);
        
    }
    
    private static List<OperationWrapper> convertPath(MarkupDocBuilder builder, OpenAPI openAPI, String path, PathItem pathItem) {
    	 
    	List<OperationWrapper>  ops=OperationWrapper.parseFromPathItem(path,pathItem);
    	return ops;
    	 
	}
    
    private static void resolveOperation(OperationWrapper op,MarkupDocBuilder builder, OpenAPI openAPI) {
    	Operation operation=op.operation;
    	String summary=operation.getSummary();
		if(StringUtils.isEmpty(summary)) {
			summary=operation.getDescription();
		}
		if(StringUtils.isEmpty(summary)) {
			return;
		}
    	builder.sectionTitleLevel(3, summary);
    	builder.textLine("请求地址:"+op.path);
    	builder.textLine("请求方式:"+op.method);
    	builder.textLine("请求参数列表:");
    	List<Parameter> parameters=operation.getParameters();
    	if(!CollectionUtils.isEmpty(parameters)) {
			List<List<String>> cells=parameters.stream()
    		    .map(App::parameterToTableCells)
    		    .collect(Collectors.toList());
    		builder.tableWithColumnSpecs(columnSpecs, cells);
    	}else {
    		if(operation.getRequestBody()==null) {
    		    builder.textLine("无请求参数");
    		}
    	}
    	RequestBody requestBody=operation.getRequestBody();
    	if(requestBody!=null) {
    		requestBody.getContent().forEach((ss,mt)->{
    			 Schema schema=mt.getSchema();
      		   if(schema!=null) {
      			   if(schema.get$ref()!=null) {
      				   String refKey=OpenAPIUtils.refToKey(schema.get$ref());
      				   schema=openAPI.getComponents().getSchemas().get(refKey);
      			   }
         		    SchemaTable tables=schemaToTableCells(null,openAPI,schema);
         		    builder.textLine("请求体类型:"+ss);
         		    tables.render(builder);
         		}
    		});
    	}
    	builder.textLine("返回值:");
    	ApiResponses resps=operation.getResponses();
    	resps.forEach((key1,resp)->{
    		String ref=resp.get$ref();
    		resp.getContent().forEach((ss,mt)->{
    		   Schema schema=mt.getSchema();
    		   if(schema!=null) {
    			   if(schema.get$ref()!=null) {
    				   String refKey=OpenAPIUtils.refToKey(schema.get$ref());
    				   schema=openAPI.getComponents().getSchemas().get(refKey);
    			   }
       		    SchemaTable tables=schemaToTableCells(null,openAPI,schema);
       		    builder.textLine("返回类型:"+ss);
       		    tables.render(builder);
       		}
    		});
    	});
	}

	static class SchemaTable{
    	String tableName;
    	List<List<String>> tables;
    	List<SchemaTable> subTables=new ArrayList<>();
    	public void render(MarkupDocBuilder builder) {
    		if(tableName!=null) {
    		    builder.textLine(tableName);
    		}
    		if(!CollectionUtils.isEmpty(tables)) {
    		    builder.tableWithColumnSpecs(columnSpecs, tables);
    		}
    		for(SchemaTable sti:subTables) {
    			sti.render(builder);
    		}
    		
    	}
    }
    
    public static SchemaTable schemaToTableCells(String tableName,OpenAPI openAPI,Schema schema){
    	
    	SchemaTable sti=new SchemaTable();
    	sti.tableName=tableName;
    	Map<Object,Object> map=schema.getProperties();
    	if(map==null) {
    		return new SchemaTable();
    	}
    	sti.tables=map.entrySet().stream()
    	    .map((e)->{
    	    	Optional<SchemaTable> optionalSchemaTableInfo=resolveProperty(openAPI,e);
    	    	optionalSchemaTableInfo.ifPresent(sti.subTables::add);
    	    	String refTable=optionalSchemaTableInfo.isPresent()?optionalSchemaTableInfo.get().tableName:null;
    		return schemaPropertyToCells(e.getKey(),e.getValue(),refTable);
    	})
    	 .collect(Collectors.toList());
    	
    	return sti;
    	
    }
    static Optional<SchemaTable> resolveProperty(OpenAPI openAPI,Entry<Object,Object> e) {
    	String keyName=(String)e.getKey();
    	Schema ss=(Schema)e.getValue();
    	if("object".equals(ss.getType())) {
    		SchemaTable sti= schemaToTableCells(keyName,openAPI,ss);
    		return Optional.of(sti);
    	}else if(ss instanceof ArraySchema) {
    		ArraySchema as=(ArraySchema)ss;
    		Schema<?> itemSchema=as.getItems();
    		if(itemSchema.get$ref()!=null) {
    			String refKey=OpenAPIUtils.refToKey(itemSchema.get$ref());
    			itemSchema=openAPI.getComponents().getSchemas().get(refKey);
    		}
    		if(itemSchema!=null) {
    			SchemaTable sti=schemaToTableCells(keyName,openAPI, itemSchema);
        		return Optional.of(sti);
    		}else {
    			logger.error("haha");
    		}
    		
    	}
    	return Optional.empty();
    }
    static List<String> schemaPropertyToCells(Object key,Object value,String refTable){
    	List<String> s=new ArrayList<>();
		s.add((String)key);
		Schema ss=(Schema) value;
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
    static List<MarkupTableColumn> columnSpecs=new ArrayList<MarkupTableColumn>();
    static {
    	columnSpecs.add(new MarkupTableColumn("字段名称"));
    	columnSpecs.add(new MarkupTableColumn("类型"));
    	columnSpecs.add(new MarkupTableColumn("是否必填"));
    	columnSpecs.add(new MarkupTableColumn("约束限制"));
    	columnSpecs.add(new MarkupTableColumn("参数位置"));
    	columnSpecs.add(new MarkupTableColumn("备注"));
    }
    public static List<String> parameterToTableCells(Parameter p){
    	List<String> s=new ArrayList<>();
    	if(p==null) {
    		System.out.println();
    	}
    	s.add(p.getName());
    	String typeStr="未知";
    	Schema schema=p.getSchema();
    	if(schema!=null) {
    		typeStr=schema.getType();
    	}
    	s.add(typeStr);
    	s.add(p.getRequired()?"是":"否");
    	s.add("暂无");
    	s.add(p.getIn());
    	s.add(p.getDescription());
    	return s;
    }
    
	
}
