package com.hngd.doc.core.web;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.hngd.openapi.entity.HttpInterface;
import com.hngd.parser.entity.ModuleInfo;
import com.hngd.parser.source.SourceParser;
 

public class ModuleParserTest {

	
	@Test
	public void testParse(){
		File f=new File("../core-test/src/main/java/com/hngd/web/controller/RoleController.java");
		List<ModuleInfo> list= SourceParser.parse(f);
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(list));
	}

 
	
}
