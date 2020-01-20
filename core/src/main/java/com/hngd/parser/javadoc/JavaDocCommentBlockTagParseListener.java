package com.hngd.parser.javadoc;

import com.hngd.parser.entity.CommentDecoratedTarget;
/**
 * Java Doc注释解析接口
 * @author tqd
 *
 * @param <T>
 */
public interface JavaDocCommentBlockTagParseListener {

	public String onParseStart(String line);
	public void onParseEnd(CommentDecoratedTarget baseInfo) ;
	public BlockTag getResult();
}
