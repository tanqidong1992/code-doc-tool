package com.hngd.openapi.entity;

import java.util.ArrayList;
import java.util.List;

import com.hngd.parser.entity.CommentDecoratedTarget;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 模块信息
 * @author tqd
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ModuleInfo extends CommentDecoratedTarget{
 
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 模块基础路径
     */
    private String url;
    /**
     * 模块接口列表
     */
    private List<HttpInterface> interfaceInfos=new ArrayList<HttpInterface>();
    /**
     * 模块类名称
     */
    private String simpleClassName;
    /**
     * 模块所在类全路径
     */
    private String canonicalClassName;
    /**
     * 是否被弃用
     */
    private Boolean deprecated;
    /**
     * 作者
     */
    private String author;
    /**
     * since
     */
    private String since;
     
}
