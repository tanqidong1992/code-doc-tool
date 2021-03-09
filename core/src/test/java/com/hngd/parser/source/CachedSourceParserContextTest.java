package com.hngd.parser.source;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

public class CachedSourceParserContextTest {

    @Test
    public void testWithCached() {
        
        CachedSourceParserContext c=new CachedSourceParserContext("test-output");
        c.initSourceInJar(Arrays.asList(new File("./test-data/postgresql-42.2.16-sources.jar")));
        c.initSource(new File("./src/test/java"));
    }
    
    @Test
    public void testWithoutCache() {
        
        SourceParserContext c=new SourceParserContext();
        c.initSourceInJar(Arrays.asList(new File("./test-data/postgresql-42.2.16-sources.jar")));
        c.initSource(new File("./src/test/java"));
    }

}
