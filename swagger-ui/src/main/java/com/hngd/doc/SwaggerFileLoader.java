package com.hngd.doc;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.hngd.common.error.ErrorCode;
import com.hngd.common.result.Result;
import com.hngd.common.result.Results;
import com.hngd.common.util.GsonUtils;
import com.hngd.doc.constants.Constants;
import com.hngd.doc.controller.DocumentController;
import com.hngd.doc.entity.DocumentInfo;
import com.hngd.doc.entity.MyTag;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;

public class SwaggerFileLoader {
    private static final Logger logger=LoggerFactory.getLogger(SwaggerFileLoader.class);
    public static File root=new File("./data").getAbsoluteFile();
    public static File history=new File("./history").getAbsoluteFile();
    
    static {
        if(!root.exists()) {
            root.mkdirs();
        }
        if(!history.exists()) {
            history.mkdirs();
        }
    }
    
    public static Map<String,DocumentInfo> documents=new ConcurrentHashMap<>();
    
    public static String buildKey(DocumentInfo di) {
        return di.getTitle();
    }


    public static void loadOriginFile(){
        if(!documents.isEmpty()) {
            return;
        }
        List<DocumentInfo> docs=Arrays.asList(root.listFiles())
            .stream().parallel()
            .map(SwaggerFileLoader::readDocument)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(d->d.getLastUpdateTime()!=null && d.getTags()!=null)
            .sorted(Comparator.comparing(DocumentInfo::getLastUpdateTime).reversed())
            .map(d->{
                d.getTags().sort(Comparator.comparing(MyTag::getName));
                return d;
            })
            .collect(Collectors.toList());
        docs.forEach(d->addToDocuments(d));
        /**
        di.forEach(d->{
            d.getTags().sort(Comparator.comparing(MyTag::getName));
        });
        */
        //di.sort(Comparator.comparing(DocumentInfo::getLastUpdateTime).reversed());
         
    }
    public static Optional<DocumentInfo> readDocument(File file) {
        OpenAPI openAPI=loadFromFile(file);
        DocumentInfo di=new DocumentInfo();
        di.setFilename(file.getName());
        di.setLastUpdateTime(file.lastModified());
        Info info=openAPI.getInfo();
        if(info==null) {
            return  Optional.empty();
        }
        di.setTitle(info.getTitle());
        di.setVersion(info.getVersion());
        List<Tag> tags=openAPI.getTags();
        if(tags!=null) {
            String filename=file.getName();
            List<MyTag> myTags=tags.stream()
                .filter(tag->tag.getName()!=null)
                .map(tag->MyTag.fromTag(filename, tag))
                .collect(Collectors.toList());
            di.setTags(myTags);
        }else {
            di.setTags(Collections.emptyList());
            logger.error("the document:{} tags is empty",file.getName());
        }
        return Optional.ofNullable(di);
    }
    public static List<DocumentInfo> loadAll(){
        if(documents.isEmpty()) {
            loadOriginFile();
        }
        List<DocumentInfo> di=new ArrayList<>(documents.values());
         
        return di;
    }
    public static void addToDocuments(DocumentInfo d) {
        String key=buildKey(d);
        DocumentInfo od=documents.get(key);
        if(od==null) {
            documents.put(key,d);
        }else {
            if(d.getLastUpdateTime()>od.getLastUpdateTime()) {
                documents.put(key,d);
                moveToHistory(od);
            }
        }
        
    }
    public static void moveToHistory(DocumentInfo od) {
        File f=new File(root,od.getFilename());
        File bk=new File(history,od.getFilename());
        try {
            FileUtils.moveFileToDirectory(f, history, true);
        } catch (IOException e) {
            logger.error("",e);
        }
        
    }
    public static OpenAPI loadFromFile(File file) {
        String s=null;
        try {
            s = FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            logger.error("",e);
        }
        SimpleType valueType=ReferenceType.construct(OpenAPI.class);
        OpenAPI openAPI=new OpenAPI();
        try {
            openAPI = Json.mapper().readValue(s, valueType);
        } catch (IOException e) {
            logger.error("",e);
        }
        return openAPI;
    }
    public static String toJson(OpenAPI openAPI) throws JsonProcessingException {
        return Json.pretty(openAPI);
    }
    public static void reloadAll() {
        documents.clear();
    }
    public static Result<Void> isValidFile(MultipartFile file) throws IOException {
        byte[] data=file.getBytes();
        String s=new String(data,"utf-8");
        OpenAPI openAPI=null;
        try {
            openAPI=GsonUtils.toObject(s, OpenAPI.class);
        }catch( Exception e) {
            logger.error("",e);
        }
        if(openAPI==null) {
            return Results.newFailResult(ErrorCode.INVALID_PARAMETER, "文档不符合《OpenAPI Specification 3.0.2》规范");
        }
        Info info=openAPI.getInfo();
        if(info==null) {
            return Results.newFailResult(ErrorCode.INVALID_PARAMETER, "文档的基本信息缺失");
        }
        List<Tag> tags=openAPI.getTags();
        return Results.newSuccessResult(null);
    }


    public static void addOriginFile(File dest) {
        Optional<DocumentInfo> documentInfo=readDocument(dest);
        if(documentInfo.isPresent()) {
            DocumentInfo doc=documentInfo.get();
            addToDocuments(doc);
        }
    }

    public static void deleteOriginFile(File file) {
        List<DocumentInfo> docs=loadAll();
        Optional<DocumentInfo> toDeleteDocument=docs.stream()
            .filter(d->d.getFilename().equals(file.getName()))
            .findFirst();
        toDeleteDocument.ifPresent(doc->{
            documents.remove(buildKey(doc));
        });
         
    }

}
