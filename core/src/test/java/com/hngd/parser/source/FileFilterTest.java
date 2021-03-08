package com.hngd.parser.source;

import java.io.File;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class FileFilterTest {

    @Test
    public void test() {
        File directory=new File(".");
        String includes="**/*.java";
        boolean includeBasedir=true;
        String excludes="**/test/**/*.java,**/com/hngd/dao/*.*";
        FileFilter ff=new FileFilter(includes, excludes);
        Collection<File> filteredFiles=ff.filterFiles(directory);
        filteredFiles.forEach(f->{
            Assert.assertTrue(f.getName().endsWith(".java"));
            Assert.assertTrue(!f.getName().contains("/test/"));
        });
        
    }
    
    @Test
    public void isInclude() {
        String includes="**/*.java";
        boolean includeBasedir=true;
        String excludes="**/test/**/*.java,**/com/hngd/dao/*.*";
        FileFilter ff=new FileFilter(includes, excludes);
        boolean ret=ff.isInclude("/work/workspaces/build-tools/hn-code-tool/core/./src/test/java/com/hngd/test/dto/Person.java");
        Assert.assertTrue(!ret);
    }
}
