package com.hngd.s2m;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.s2m.constant.Constants;
import com.hngd.s2m.entity.OperationWrapper;
import com.hngd.s2m.exception.GenerateException;
import com.hngd.s2m.utils.OpenAPIUtils;
import com.hngd.s2m.utils.SchemaResolver;

import io.github.swagger2markup.markup.builder.MarkupDocBuilder;
import io.github.swagger2markup.markup.builder.MarkupDocBuilders;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import com.hngd.s2m.entity.SchemaTable;

public class OpenAPIToMarkdown {

    private static final Logger logger=LoggerFactory.getLogger(OpenAPIToMarkdown.class);
    public static File openAPIToMarkdown(File openAPIFile,List<String> includeTags,File outputDirectory) {
        OpenAPI openAPI=OpenAPIUtils.loadFromFile(openAPIFile);
        return openAPIToMarkdown(openAPI, includeTags, outputDirectory);
    }
    public static File openAPIToMarkdown(OpenAPI openAPI,List<String> includeTags,File outputDirectory){
        MarkupDocBuilder builder = MarkupDocBuilders.documentBuilder(MarkupLanguage.MARKDOWN);
        Paths paths=openAPI.getPaths();
        Map<String,List<OperationWrapper>> moduleOps=new HashMap<>();
        paths.forEach((key,pathItem)->{
            List<OperationWrapper> ops=convertPath(builder,openAPI,key,pathItem);
            if(!CollectionUtils.isEmpty(ops)) {
                ops.forEach(op->{
                    List<String> tags=op.operation.getTags();
                    for(String tag:tags) {
                        List<OperationWrapper> tagOps=moduleOps.get(tag);
                        if(tagOps==null) {
                            tagOps=new ArrayList<>();
                            moduleOps.put(tag, tagOps);
                        }
                        tagOps.add(op);
                    }
                });
            }
        });
        Info info=openAPI.getInfo();
        if(info==null) {
            throw new GenerateException("????????????????????????", null);
        }
        
        builder.documentTitle(info.getTitle()+info.getVersion());
        builder.textLine(info.getDescription());
        List<Server> servers=openAPI.getServers();
        if(servers!=null){
            servers.forEach(server -> {
                builder.textLine("("+server.getDescription()+")?????????????????????"+server.getUrl());
            });
        }
        moduleOps.forEach((tag,ops)->{
            if(includeTags==null || includeTags.contains(tag)) {
                builder.sectionTitleLevel(2, tag);
                if(!CollectionUtils.isEmpty(ops)) {
                    ops.forEach(op->resolveOperation(op,builder,openAPI));
                }
            }
            
        });
        String tagNameSuffix=null;
        if(CollectionUtils.isNotEmpty(includeTags)) {
            tagNameSuffix=StringUtils.join(includeTags, "-");
        }
        String title=info.getTitle();
        //?????????,??????????????????
        if(tagNameSuffix!=null && tagNameSuffix.length()<10) {
            title+="-"+tagNameSuffix;
        }
        File output=new File(outputDirectory,title+"-"+info.getVersion()+".md");
        if(output.exists()) {
            output.delete();
        }
        output=output.getAbsoluteFile();
        builder.writeToFileWithoutExtension(output.toPath(), StandardCharsets.UTF_8,StandardOpenOption.CREATE_NEW);
        return output;
    }
    
    private static List<OperationWrapper> convertPath(MarkupDocBuilder builder, OpenAPI openAPI, String path, PathItem pathItem) {
         
        List<OperationWrapper>  ops=OperationWrapper.parseFromPathItem(path,pathItem);
        return ops;
         
    }
    private static void resolveOperation(OperationWrapper op,MarkupDocBuilder builder, OpenAPI openAPI) {
        try {
            doResolveOperation(op, builder, openAPI);
        }catch(Throwable e) {
            throw new GenerateException("????????????:"+op.path+" "+op.method+"??????:", e);
        }
    }
    private static void doResolveOperation(OperationWrapper op,MarkupDocBuilder builder, OpenAPI openAPI) {
        Operation operation=op.operation;
        String summary=operation.getSummary();
        if(StringUtils.isEmpty(summary)) {
            summary=operation.getDescription();
        }
        if(StringUtils.isEmpty(summary)) {
            return;
        }
        builder.sectionTitleLevel(3, summary);
        builder.textLine("????????????:"+op.path);
        builder.textLine("????????????:"+op.method);
        builder.textLine("??????????????????:");
        List<Parameter> parameters=operation.getParameters();
        if(!CollectionUtils.isEmpty(parameters)) {
            List<List<String>> cells=parameters.stream()
                .map(OpenAPIToMarkdown::parameterToTableCells)
                .collect(Collectors.toList());
            builder.tableWithColumnSpecs(Constants.columnSpecs, cells);
        }else {
            if(operation.getRequestBody()==null) {
                builder.textLine("???????????????");
            }
        }
        RequestBody requestBody=operation.getRequestBody();
        if(requestBody!=null) {
            requestBody.getContent().forEach((ss,mt)->{
                 Schema<?> schema=mt.getSchema();
                 if(schema!=null) {
                     if(schema.get$ref()!=null) {
                         String refKey=OpenAPIUtils.refToKey(schema.get$ref());
                         schema=openAPI.getComponents().getSchemas().get(refKey);
                     }
                     
                    Optional<SchemaTable> optionalTables=SchemaResolver
                            .newSchemaResolver(openAPI)
                            .schemaToTableCells(null,schema);
                    optionalTables.ifPresent(tables->{
                       builder.textLine("???????????????:"+ss);
                         tables.render(builder);
                    });
                     
                 }
            });
        }
        builder.textLine("?????????:");
        ApiResponses resps=operation.getResponses();
        resps.forEach((key1,resp)->{
            String ref=resp.get$ref();
            resp.getContent().forEach((ss,mt)->{
               Schema<?> schema=mt.getSchema();
               if(schema==null) {
                   return;
               }
               if(schema.get$ref()!=null) {
                    String refKey=OpenAPIUtils.refToKey(schema.get$ref());
                    schema=openAPI.getComponents().getSchemas().get(refKey);
               }
                  Optional<SchemaTable> optionalTables=SchemaResolver
                        .newSchemaResolver(openAPI)
                        .schemaToTableCells(null,schema);
                  optionalTables.ifPresent(tables->{
                      builder.textLine("????????????:"+ss);
                      tables.render(builder);
                  });
                   
                
            });
        });
    }
 
    
    


    public static List<String> parameterToTableCells(Parameter p){
        List<String> s=new ArrayList<>();
        if(p==null) {
            System.out.println();
        }
        s.add(p.getName());
        String typeStr="??????";
        Schema<?> schema=p.getSchema();
        if(schema!=null) {
            typeStr=schema.getType();
        }
        s.add(typeStr);
        s.add(p.getRequired()?"???":"???");
        s.add("??????");
        s.add(p.getIn());
        s.add(p.getDescription());
        return s;
    }
}
