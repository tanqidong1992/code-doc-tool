/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：ClassCommentParser.java
 * @时间：2017年2月14日 下午4:15:09
 * @作者：
 * @备注：
 * @版本:
 */

package com.hngd.parser.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author
 */
public class ClassCommentParser {
	/**
	 * java doc comment must has two lines
	 */
	public static final int JAVADOC_COMMENT_MIN_LINE_SIZE=2;
	private static final Logger logger = LoggerFactory.getLogger(ClassCommentParser.class);

	static class ParseResult {
		int startIndex;
		int endIndex = -1;
		CommentElement element;
	}

	public static List<CommentElement> parseMethodComment(List<String> commentLines) {
		if (commentLines.size() <= JAVADOC_COMMENT_MIN_LINE_SIZE) {
			return Collections.emptyList();
		}
		List<String> lines = commentLines.subList(1, commentLines.size() - 1);
		List<CommentElement> pce = new ArrayList<>();
		ParseResult pr = parseDescription(lines);
		pce.add(pr.element);
		while (pr.endIndex < lines.size()) {
			pr = parseElement(lines, pr.endIndex);
			pce.add(pr.element);
		}
		return pce;
	}
    
	private static ParseResult parseDescription(List<String> lines) {
		ParseResult pr = new ParseResult();
		pr.startIndex = 0;
		StringBuilder desc = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			line = line.replaceFirst("\\*", "").trim();
			if (line.startsWith("@")) {
				pr.endIndex = i;
				break;
			}
			desc.append(line);
		}
		CommentElement ce = new CommentElement.DefaultCommentElement();
		ce.comment = desc.toString();
		pr.element = ce;
		if (pr.endIndex == -1) {
			pr.endIndex = lines.size();
		}
		return pr;
	}

	private static ParseResult parseElement(List<String> lines, int startIndex) {
		CommentElement ce = null;
		ParseResult pr = new ParseResult();
		pr.startIndex = startIndex;
		StringBuilder desc = new StringBuilder();
		boolean isFoundAt = false;
		for (int i = startIndex; i < lines.size(); i++) {
			if (StringUtils.isEmpty(lines.get(i))) {
				continue;
			}
			String line = lines.get(i).replaceFirst("\\*", "").trim();
			if (line.startsWith("@")) {
				if (isFoundAt) {
					pr.endIndex = i;
					break;
				} else {
					ce=findElementParser(line);
					if (ce != null) {
						line = ce.onParseStart(line);
					} else {
						logger.error("could not parse line:{}",line);
					}
					isFoundAt = true;
				}
			}
			desc.append(line);
		}
		if (pr.endIndex == -1) {
			pr.endIndex = lines.size();
		}
		pr.element = ce;
		if (ce != null) {
			ce.comment = desc.toString();
		}
		return pr;
	}
	
	public static CommentElement findElementParser(String line) {
		Set<String> keys = CommentElement.commentElements.keySet();
		for (String key : keys) {
			if (line.startsWith(key)) {
				CommentElement ceType = CommentElement.commentElements.get(key);
				CommentElement ce=null;
				try {
					ce = ceType.getClass().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					logger.error("", e);
				}
				if(ce!=null) {
					return ce;
				}
			}
		}
		return null;
	}
}
