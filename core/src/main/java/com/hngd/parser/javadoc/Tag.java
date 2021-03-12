package com.hngd.parser.javadoc;

import java.util.Objects;

public class Tag extends JavaDocCommentElement{

    private static final long serialVersionUID = 1L;

    public static final String TAG_PREFIX="@";
    
    protected String keyword;
    
    public Tag(String keyword) {
        Objects.requireNonNull(keyword);
        if(!keyword.startsWith(TAG_PREFIX)) {
            throw new RuntimeException("Java Doc Comment Block keyword must start with "+TAG_PREFIX);
        }
        this.keyword = keyword;
    }
    public String getKeyword() {
        return keyword;
    }
}
