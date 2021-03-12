/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：CommentElement.java
 * @时间：2017年2月14日 下午4:11:35
 * @作者：
 * @备注：
 * @版本:
 */

package com.hngd.parser.javadoc;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.hngd.parser.entity.CommentDecoratedTarget;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.entity.ParameterInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** 
 * @author
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlockTag extends JavaDocCommentElement implements BlockTagParseListener{
 
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String TAG_PREFIX="@";
    
    private String tag;
    /**
     * @param tag
     * @author
     * @since 0.0.1
     */
    public BlockTag(String tag) {
        Objects.requireNonNull(tag);
        if(!tag.startsWith(TAG_PREFIX)) {
            throw new RuntimeException("Java Doc Comment Block Tag must start with "+TAG_PREFIX);
        }
        this.tag = tag;
        BlockTagParserContext.register(tag, this.getClass());
    }
    
    public void onParseEnd(CommentDecoratedTarget parent) {
        String key=tag;
        key=key.replace(TAG_PREFIX, "");
        if(StringUtils.isNotBlank(getContent())) {
            parent.addExtension(key, this);
        }
    }
    public String onParseStart(String startLine) {
        return startLine.replace(tag, "").trim();
    }
    @Override
    public BlockTag getResult() {
        return this;
    }
    
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ParamBlockTag extends BlockTag {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private String paramName;
        public ParamBlockTag() {
            super("@param");
        }
        @Override
        public String onParseStart(String line) {
            line = super.onParseStart(line);
            int index = line.indexOf(" ");
            if(index>0) {
                paramName=line.substring(0, index);
                return line.substring(index, line.length()).trim();
            }else {
                paramName=line;
                return "";
            }
        }
        @Override
        public void onParseEnd(CommentDecoratedTarget parent) {
            if(parent instanceof MethodInfo) {
                ParameterInfo parameterInfo = new ParameterInfo();
                ParamBlockTag paramElement = this;
                parameterInfo.setName(paramElement.paramName);
                parameterInfo.setComment(paramElement.getContent());
                ((MethodInfo)parent).getParameters().add(parameterInfo);
            }
        }
    }

    public static class ReturnBlockTag extends BlockTag {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public ReturnBlockTag() {
            super("@return");
        }
        
        @Override
        public void onParseEnd(CommentDecoratedTarget target) {
            if(target instanceof MethodInfo) {
                ((MethodInfo)target).setRetComment(this.getContent());
            }
        }
    }

    public static class SeeBlockTag extends BlockTag {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public SeeBlockTag() {
            super("@see");
        }
    }
  
    public static class ThrowsBlockTag extends BlockTag {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public ThrowsBlockTag() {
            super("@throws");
        }
    }

    public static class SinceBlockTag extends BlockTag {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public SinceBlockTag() {
            super("@since");
        }
    }
    public static class AuthorBlockTag extends BlockTag{
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public AuthorBlockTag(){
            super("@author");
        }
    }
}
