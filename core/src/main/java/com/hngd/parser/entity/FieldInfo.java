package com.hngd.parser.entity;

import com.github.javaparser.ast.body.FieldDeclaration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *类成员变量信息
 * @author tqd
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class FieldInfo extends CommentDecoratedTarget{
 
    private static final long serialVersionUID = 1L;
    
    public FieldInfo(String comment, String fieldName, FieldDeclaration fieldDetail) {
        this.setComment(comment)
            .setName(fieldName);
        this.fieldDetail = fieldDetail;
    }
    /**
     * 类成员变量声明代码
     */
    private transient FieldDeclaration fieldDetail;
    
}
