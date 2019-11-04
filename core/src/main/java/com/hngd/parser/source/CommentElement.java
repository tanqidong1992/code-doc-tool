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

import java.util.HashMap;
import java.util.Map;

/**
 * @author
 */
public class CommentElement {
	static Map<String, CommentElement> commentElements = new HashMap<>();
	static {
		// register parse type
		new ParamElement();
		new AuthorElement();
		new ReturnElement();
		new SeeElement();
		new SinceElement();
		new ThrowsElement();
	}

	public String comment;
	public String prefix;

	/**
	 * @param prefix
	 * @author
	 * @since 0.0.1
	 */
	public CommentElement(String prefix) {
		this.prefix = prefix;
		synchronized (commentElements) {
			if (!commentElements.containsKey(prefix)) {
				commentElements.put(prefix, this);
			}
		}
	}

	public String parse(String line) {
		return line.replace(prefix, "").trim();
	}

	public static class DescElement extends CommentElement {
		public static final String prefix = "@desc";
		String paramName;

		public DescElement() {
			super(prefix);
		}

		@Override
		public String parse(String line) {
			return line;
		}
	}

	public static class ParamElement extends CommentElement {
		public static final String prefix = "@param";
		String paramName;

		public ParamElement() {
			super(prefix);
		}

		@Override
		public String parse(String line) {
			line = super.parse(line);
			int index = line.indexOf(" ");
			paramName = index > 0 ? line.substring(0, index) : line;
			return index > 0 ? line.substring(index, line.length()) : line;
		}
	}

	public static class ReturnElement extends CommentElement {
		public ReturnElement() {
			super(prefix);
		}

		public static final String prefix = "@return";
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
