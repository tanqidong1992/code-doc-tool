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
import com.hngd.doc.core.InterfaceInfo;
import com.hngd.doc.core.InterfaceInfo.RequestParameterInfo;
import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.parse.ModuleParser;
 

public class ModuleParserTest {

	
	@Test
	public void testParse(){
		File f=new File("E:\\Code\\STSCode\\hnvmns-auth\\src\\main\\java\\com\\hngd\\web\\controller\\UserController.java");
		List<ModuleInfo> list= ModuleParser.parse(f);
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(list));
	}

 
	
}
