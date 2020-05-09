package com.hngd.s2m;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.hngd.s2m.OpenAPIToMarkdown.SchemaTable;
import com.hngd.s2m.utils.OpenAPIUtils;

import io.github.swagger2markup.markup.builder.MarkupDocBuilder;
import io.github.swagger2markup.markup.builder.MarkupDocBuilders;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
 

/**
 * Unit test for simple App.
 */
public class AppTest {
	
   @Test
   public void test() {
	   File file=new File("./test-data/api.json");
       OpenAPI openAPI=OpenAPIUtils.loadFromFile(file);
      
       //File outputDirectory=new File("test-output");
       //OpenAPIToMarkdown.openAPIToMarkdown(openAPI,null, outputDirectory);
       Schema schema=openAPI.getComponents().getSchemas().get("RestResponseComplexDTO");
       SchemaTable st=OpenAPIToMarkdown.schemaToTableCells("", openAPI, schema);
       MarkupDocBuilder builder = MarkupDocBuilders.documentBuilder(MarkupLanguage.MARKDOWN);
       
        st.render(builder);
        System.out.println(builder.toString());
   }
}
