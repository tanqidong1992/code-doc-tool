package com.hngd.parser.javadoc;

import com.hngd.parser.entity.BaseInfo;
/**
 * Java Doc注释解析接口
 * @author tqd
 *
 * @param <T>
 */
public interface JavaDocCommentBlockTagParser {

	public String onParseStart(String line);
	public void onParseEnd(BaseInfo baseInfo) ;
	public BlockTag getResult();
}
