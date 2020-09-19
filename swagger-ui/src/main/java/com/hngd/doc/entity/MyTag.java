package com.hngd.doc.entity;

import io.swagger.v3.oas.models.tags.Tag;
import lombok.Data;

@Data
public class MyTag {
    private String name;
    private String query;
    public static MyTag fromTag(String filename, Tag tag) {
        MyTag mt=new MyTag();
        mt.query= filename+"/"+tag.getName();
        mt.name=tag.getName();
        return mt;
    }
}
