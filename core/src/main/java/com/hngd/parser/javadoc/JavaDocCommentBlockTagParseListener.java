package com.hngd.parser.javadoc;

import com.hngd.parser.entity.CommentDecoratedTarget;
/**
 * Java Doc注释解析事件监听接口
 * @author tqd
 */
public interface JavaDocCommentBlockTagParseListener {
    /**
     * 当block tags被解析器匹配时,调用此函数
     * @param firstLine 当block tags被解析器匹配时,传入的第一行注释
     * @return
     */
    public String onParseStart(String firstLine);
    /**
     * 
     * @param target
     */
    public void onParseEnd(CommentDecoratedTarget target) ;
    /**
     * 
     * @return
     */
    public BlockTag getResult();
}
