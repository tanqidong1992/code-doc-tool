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

package com.hngd.parser.source;

import org.apache.commons.lang3.StringUtils;

import com.hngd.parser.entity.BaseInfo;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.entity.ParameterInfo;

/**
 * @author
 */
public class CommentElement implements CommentElementParser{
	

	public String comment;
	public String prefix;

	/**
	 * @param prefix
	 * @author
	 * @since 0.0.1
	 */
	public CommentElement(String prefix) {
		this.prefix = prefix;
		CommentElementParserContext.register(prefix, this.getClass());
	}
    
	public void onParseEnd(BaseInfo baseInfo) {
		String key=prefix;
		if(key.startsWith("@")) {
			key=key.replace("@", "");
		}
		if(StringUtils.isNotBlank(comment)) {
			baseInfo.addExtension(key, comment);
		}
		
	}
	public String onParseStart(String line) {
		return line.replace(prefix, "").trim();
	}
	public static class DefaultCommentElement extends CommentElement implements CommentElementParser{
		//TODO delete it
		public static final String prefix = "@desc";
		String paramName;
		public DefaultCommentElement() {
			super(prefix);
		}
		@Override
		public String onParseStart(String line) {
			return line;
		}
		@Override
		public void onParseEnd(BaseInfo baseInfo) {
			baseInfo.setComment(this.comment);
		}
		
	}

	@Override
	public CommentElement getResult() {
		return this;
	}
	
	public static class ParamElement extends CommentElement {
		public static final String prefix = "@param";
		String paramName;

		public ParamElement() {
			super(prefix);
		}

		@Override
		public String onParseStart(String line) {
			line = super.onParseStart(line);
			int index = line.indexOf(" ");
			paramName = index > 0 ? line.substring(0, index) : line;
			return index > 0 ? line.substring(index, line.length()) : line;
		}
		@Override
		public void onParseEnd(BaseInfo baseInfo) {
			if(baseInfo instanceof MethodInfo) {
				
				ParameterInfo parameterInfo = new ParameterInfo();
				ParamElement paramElement = this;
				parameterInfo.setName(paramElement.paramName);
				parameterInfo.setComment(paramElement.comment);
				((MethodInfo)baseInfo).getParameters().add(parameterInfo);
			}
		}
	}

	public static class ReturnElement extends CommentElement {
		public ReturnElement() {
			super(prefix);
		}

		public static final String prefix = "@return";
		
		@Override
		public void onParseEnd(BaseInfo baseInfo) {
			((MethodInfo)baseInfo).setRetComment(this.comment);
		}
	}

	public static class SeeElement extends CommentElement {
		public static final String prefix = "@see";

		public SeeElement() {
			super(prefix);
		}
	}

	public static class AuthorElement extends CommentElement {
		public static final String prefix = "@author";
		public AuthorElement() {
			super(prefix);
		}
	}

	public static class ThrowsElement extends CommentElement {
		public static final String prefix = "@throws";
		public ThrowsElement() {
			super(prefix);
		}
	}

	public static class SinceElement extends CommentElement {
		public static final String prefix = "@since";
		public SinceElement() {
			super(prefix);
		}
	}

}
