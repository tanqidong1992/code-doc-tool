package com.hngd.parser.javadoc.extension;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hngd.parser.entity.CommentDecoratedTarget;
import com.hngd.parser.javadoc.BlockTag;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md#operationObject" >OpenAPI PathItem Tags</a> 
 * @author tqd
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TagsBlockTag extends BlockTag
{
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
