package com.hngd.parser.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 类成员函数信息
 * @author tqd
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MethodInfo extends CommentDecoratedTarget{
    /**
     * 返回注释
     */
    private String retComment;
    /**
     * 参数信息列表
     */
    private List<ParameterInfo> parameters=new ArrayList<>();
    /**
     * 作者
     */
    private String author;
    /**
     * since
     */
    private String since;
}



