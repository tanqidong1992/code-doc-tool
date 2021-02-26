package com.hngd.base;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.shared.utils.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class FileFilterTest {

    @Test
    public void test() throws IOException {
        File directory=new File(".");
        String includes="**/*.java";
        boolean includeBasedir=true;
        String excludes="**/test/**/*.java,**/com/hngd/dao/*.*";
        List<File>  files=FileUtils.getFiles(directory, includes, excludes, includeBasedir);
        files.forEach(f->{
            Assert.assertTrue(f.getName().endsWith(".java"));
            Assert.assertTrue(!f.getName().contains("/test/"));
        });
    }

}
