package com.hngd.parser.source;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.MatchPatterns;

public class SourceFileFilter {

    private MatchPatterns includePatterns;
    private MatchPatterns excludePatterns;

    public SourceFileFilter(String includes,String excludes) {

        if(StringUtils.isNotBlank(excludes)) {
            String []excludeArray=StringUtils.split(excludes, ",");
            excludePatterns=MatchPatterns.from(excludeArray);
        }
        if(StringUtils.isNotBlank(includes)) {
            String []includeArray=StringUtils.split(includes, ",");
            includePatterns=MatchPatterns.from(includeArray);
        }
    }
    
    public boolean isInclude(String name) {
        if(includePatterns==null || includePatterns.matches(name, true)) {
            if(excludePatterns==null || !excludePatterns.matches(name, true)) {
                return true;
            }
        }
        return false;
    }

    public Collection<File> filterFiles(File baseDirectory) {
        String basePath=baseDirectory.getAbsolutePath();
        IOFileFilter filter=new IOFileFilter() {

            @Override
            public boolean accept(File file) {
               String relativeName=file.getAbsolutePath().replace(basePath, "").substring(1);
               return isInclude(relativeName);
            }

            @Override
            public boolean accept(File dir, String name) {
                String fullName=dir.getAbsolutePath()+File.separator+name;
                String relativeName=fullName.replace(basePath, "").substring(1);
                return isInclude(relativeName);
            }
            
        };
        return FileUtils.listFiles(baseDirectory,filter,TrueFileFilter.INSTANCE);
    }
}
