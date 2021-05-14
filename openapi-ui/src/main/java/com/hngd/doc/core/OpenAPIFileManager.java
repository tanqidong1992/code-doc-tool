package com.hngd.doc.core;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hngd.doc.config.DocumentProperties;
import com.hngd.doc.entity.DocumentInfo;
import com.hngd.doc.entity.DocumentTag;
import com.hngd.doc.utils.DocumentUtils;

@Component
public class OpenAPIFileManager {

    private static final Logger logger=LoggerFactory.getLogger(OpenAPIFileManager.class);
    
    public static final String DATA_BASE_DIR="data";
    public static final String HISTORY_BASE_DIR="history";
    private File data;
    private File history;
    
    public File getHistory() {
        return history;
    }

    public File getData() {
        return data;
    }

    @Autowired
    private DocumentProperties config;
    
    @PostConstruct
    public void onInit() {
        String workDirectory=config.getWorkDirectory();
        data=new File(workDirectory+File.separator+DATA_BASE_DIR).getAbsoluteFile();
        history=new File(workDirectory+File.separator+HISTORY_BASE_DIR).getAbsoluteFile();
        if(!data.exists()) {
            data.mkdirs();
        }else {
            if(data.isFile()) {
                throw new RuntimeException("无法创建目录:"+data.getAbsolutePath());
            }
        }
        if(!history.exists()) {
            history.mkdirs();
        }else {
            if(history.isFile()) {
                throw new RuntimeException("无法创建目录:"+history.getAbsolutePath());
            }
        }
    }
     
    private Map<String,DocumentInfo> loadedDocuments=new ConcurrentHashMap<>();
    
    public void loadAllOpenAPIFile(){
        if(!loadedDocuments.isEmpty()) {
            return;
        }
        List<DocumentInfo> docs=Arrays.asList(data.listFiles())
            .stream().parallel()
            .map(DocumentUtils::readDocument)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(d->d.getLastUpdateTime()!=null && d.getTags()!=null)
            .sorted(Comparator.comparing(DocumentInfo::getLastUpdateTime).reversed())
            .map(d->{
                d.getTags().sort(Comparator.comparing(DocumentTag::getName));
                return d;
            })
            .collect(Collectors.toList());
        docs.forEach(d->addToDocuments(d));
    }
    
    public List<DocumentInfo> getAllDocuments(){
        if(loadedDocuments.isEmpty()) {
            loadAllOpenAPIFile();
        }
        List<DocumentInfo> di=new ArrayList<>(loadedDocuments.values());
        return di;
    }
    public void addToDocuments(DocumentInfo doc) {
        String key=DocumentUtils.buildKey(doc);
        DocumentInfo od=loadedDocuments.get(key);
        if(od==null) {
            loadedDocuments.put(key,doc);
        }else {
            if(doc.getLastUpdateTime()>od.getLastUpdateTime()) {
                loadedDocuments.put(key,doc);
                moveToHistory(od);
            }
        }
    }
    public void moveToHistory(DocumentInfo doc) {
        File docFile=new File(data,doc.getFilename());
        try {
            FileUtils.moveFileToDirectory(docFile, history, true);
        } catch (IOException e) {
            logger.error("",e);
        }
    }
 
    public void cleanAllLoadedDocument() {
        loadedDocuments.clear();
    }

    public void addOpenAPIFile(File file) {
        Optional<DocumentInfo> documentInfo=DocumentUtils.readDocument(file);
        documentInfo.ifPresent(this::addToDocuments);
    }

    public void deleteOpenAPIFile(File file) {
        List<DocumentInfo> docs=getAllDocuments();
        Optional<DocumentInfo> toDeleteDocument=docs.stream()
            .filter(d->d.getFilename().equals(file.getName()))
            .findFirst();
        toDeleteDocument.ifPresent(doc->loadedDocuments.remove(DocumentUtils.buildKey(doc)));
    }
}
