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
import com.hngd.api.http.HttpInterfaceInfo;
import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.parse.SourceParser;
 

public class ModuleParserTest {

	
	@Test
	public void testParse(){
		File f=new File("E:\\Code\\STSCode\\hnvmns-auth\\src\\main\\java\\com\\hngd\\web\\controller\\UserController.java");
		f=new File("D:\\company\\projects\\inspection-system\\education-training-system\\src\\main\\java\\com\\hngd\\web\\controller\\TrainingRecordController.java");
		List<ModuleInfo> list= SourceParser.parse(f);
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(list));
	}

 
	
}
