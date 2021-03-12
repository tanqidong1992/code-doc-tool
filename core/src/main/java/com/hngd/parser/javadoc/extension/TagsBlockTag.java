/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：MobileElement.java
 * @时间：2017年2月14日 下午5:14:03
 * @作者：
 * @备注：
 * @版本:
 */
package com.hngd.parser.javadoc.extension;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hngd.parser.entity.CommentDecoratedTarget;
import com.hngd.parser.javadoc.BlockTag;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * https://swagger.io/specification/#pathItemObject tags
 * @author tqd
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TagsBlockTag extends BlockTag
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static String TAG_DELIMITER=",";
    private List<String> pathItemTags=new ArrayList<>();
    /**
     * 
     * @author tqd
     * @since 0.0.1 
     */
    public TagsBlockTag()
    {
        super("@tags");
    }
    @Override
    public void onParseEnd(CommentDecoratedTarget parent) {
        super.onParseEnd(parent);
        String content=this.getContent();
        if(content.contains(TAG_DELIMITER)) {
            String[] items=content.split(TAG_DELIMITER);
            for(String item:items) {
                item=item.trim();
                if(StringUtils.isNotBlank(item)) {
                    pathItemTags.add(item);
                }
            }
        }else {
            pathItemTags.add(content);
        }
    }
    
    
}
