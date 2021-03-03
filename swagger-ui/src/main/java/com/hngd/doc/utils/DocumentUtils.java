package com.hngd.doc.utils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.doc.entity.DocumentInfo;
import com.hngd.doc.entity.DocumentTag;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;

public class DocumentUtils {
    
    private static final Logger logger=LoggerFactory.getLogger(DocumentUtils.class);
    
    
    public static Optional<DocumentInfo> readDocument(File file) {
        Optional<OpenAPI> openAPI=OpenAPIFileUtils.loadFromFile(file);
        if(openAPI.isEmpty()) {
            return Optional.empty();
        }
        DocumentInfo di=new DocumentInfo();
        di.setFilename(file.getName());
        di.setLastUpdateTime(file.lastModified());
        Info info=openAPI.get().getInfo();
        if(info==null) {
            return Optional.empty();
        }
        di.setTitle(info.getTitle());
        di.setVersion(info.getVersion());
        List<Tag> tags=openAPI.get().getTags();
        if(tags!=null) {
            String filename=file.getName();
            List<DocumentTag> myTags=tags.stream()
                .filter(tag->tag.getName()!=null)
                .map(tag->DocumentTag.fromTag(filename, tag))
                .collect(Collectors.toList());
            di.setTags(myTags);
        }else {
            di.setTags(Collections.emptyList());
            logger.error("the document:{} tags is empty",file.getName());
        }
        return Optional.ofNullable(di);
    }
    

    public static String buildKey(DocumentInfo di) {
        return di.getTitle();
    }

}
