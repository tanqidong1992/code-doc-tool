package com.hngd.base;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.hngd.parser.source.SourceParserContext;

public class ExternalJarSourceParseTest {

    @Test
    public void test() throws IOException {
 
        SourceParserContext pc=new SourceParserContext(null, null);
        String path1="./test-data/postgresql-42.2.16-sources.jar";
        pc.initSourceInJar(Arrays.asList(new File(path1)));
        pc.getCommentStore().print();
   
    }

}
