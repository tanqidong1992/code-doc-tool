package com.hngd.parser.source;

import com.hngd.parser.entity.BaseInfo;

public interface CommentElementParser {

	public String onParseStart(String line);
	public void onParseEnd(BaseInfo baseInfo) ;
	public CommentElement getResult();
}
