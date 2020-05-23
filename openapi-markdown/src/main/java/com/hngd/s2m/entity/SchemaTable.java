package com.hngd.s2m.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.hngd.s2m.constant.Constants;

import io.github.swagger2markup.markup.builder.MarkupDocBuilder;

public class SchemaTable {
	public String tableName;
	public List<List<String>> tables;
	public List<SchemaTable> subTables=new ArrayList<>();
	public void render(MarkupDocBuilder builder) {
		if(tableName!=null) {
		    builder.textLine(tableName);
		}
		if(!CollectionUtils.isEmpty(tables)) {
		    builder.tableWithColumnSpecs(Constants.columnSpecs, tables);
		}
		for(SchemaTable sti:subTables) {
			sti.render(builder);
		}
		
	}
}
