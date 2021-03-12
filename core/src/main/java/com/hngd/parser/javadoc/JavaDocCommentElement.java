package com.hngd.parser.javadoc;

import java.io.Serializable;

import lombok.Data;

@Data
public class JavaDocCommentElement implements Serializable{
    
    private static final long serialVersionUID = 1L;
    /**
     * 内容
     */
    private String content;
}
