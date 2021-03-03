package com.hngd.doc.entity;

import io.swagger.v3.oas.models.tags.Tag;
import lombok.Data;

@Data
public class DocumentTag {
    /**
     * 标签名称
     */
    private String name;
    /**
     * 查询路径
     */
    private String query;
    public static DocumentTag fromTag(String filename, Tag tag) {
        DocumentTag mt=new DocumentTag();
        mt.query= filename+"/"+tag.getName();
        mt.name=tag.getName();
        return mt;
    }
}
