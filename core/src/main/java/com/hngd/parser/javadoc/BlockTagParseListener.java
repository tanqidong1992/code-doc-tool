package com.hngd.parser.javadoc;

import com.hngd.parser.entity.CommentDecoratedTarget;
/**
 * Java Doc注释解析事件监听接口
 * @author tqd
 */
public interface BlockTagParseListener {
    /**
     * 当block tag被解析器匹配时,调用此函数
     * @param startLine 当block tags被解析器匹配时,传入的第一行注释
     * @return
     */
    public String onParseStart(String startLine);
    /**
     * 
     * @param parent
     */
    public void onParseEnd(CommentDecoratedTarget parent) ;
    /**
     * 
     * @return
     */
    public BlockTag getResult();
}
