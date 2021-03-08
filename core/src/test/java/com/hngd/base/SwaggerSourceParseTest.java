package com.hngd.base;

import java.io.File;
import java.io.IOException;

import com.hngd.parser.source.SourceParserContext;

public class SwaggerSourceParseTest {

    public static void main(String[] args) throws IOException {
        
        String filePath="../swagger-ui/src/main/java";
        File sourceBaseDirectory=new File(filePath);
        SourceParserContext pc=new SourceParserContext("com/hngd/**/*.java", null);
        String path1="D:\\app\\maven-repo\\org\\postgresql\\postgresql\\42.2.2\\postgresql-42.2.2-sources.jar";
        pc.initSource(sourceBaseDirectory);
        pc.getCommentStore().print();
     
        
        
         
    }

}
