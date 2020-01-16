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
import java.util.Optional;

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
		public boolean hasElement() {
			return element!=null;
		}
	}

	public static List<CommentElement> parseMethodComment(List<String> commentLines) {
		if (commentLines.size() <= JAVADOC_COMMENT_MIN_LINE_SIZE) {
			return Collections.emptyList();
		}
		//remove the first line and last line
		List<String> lines = commentLines.subList(1, commentLines.size() - 1);
		List<CommentElement> commentElements = new ArrayList<>();
		ParseResult result = parseDescription(lines);
		if(result.hasElement()) {
			commentElements.add(result.element);
		}
		while (result.endIndex < lines.size()) {
			result = parseElement(lines, result.endIndex);
			if(result.hasElement()) {
			    commentElements.add(result.element);
			}
		}
		return commentElements;
	}
    
	private static ParseResult parseDescription(List<String> lines) {
		ParseResult result = new ParseResult();
		result.startIndex = 0;
		StringBuilder desc = new StringBuilder();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			line = line.replaceFirst("\\*", "").trim();
			if (line.startsWith("@")) {
				result.endIndex = i;
				break;
			}
			if(StringUtils.isNoneBlank(line)) {
				desc.append(line);
			}
		}
		CommentElement commentElement = new CommentElement.DefaultCommentElement();
		commentElement.comment = desc.toString();
		result.element = commentElement;
		if (result.endIndex == -1) {
			result.endIndex = lines.size();
		}
		return result;
	}

	private static ParseResult parseElement(List<String> lines, int startIndex) {
		CommentElement element = null;
		ParseResult parseResult = new ParseResult();
		parseResult.startIndex = startIndex;
		StringBuilder elementComment = new StringBuilder();
		boolean isFoundAt = false;
		for (int i = startIndex; i < lines.size(); i++) {
			if (StringUtils.isEmpty(lines.get(i))) {
				continue;
			}
			String line = lines.get(i).replaceFirst("\\*", "").trim();
			if(!line.startsWith("@")) {
				elementComment.append(line);
				continue;
			}
			if (isFoundAt) {
				parseResult.endIndex = i;
				break;
			} else {
				Optional<CommentElementParser> optionalElementParser = CommentElementParserContext.findElementParser(line);
				if (optionalElementParser.isPresent()) {
					line = optionalElementParser.get().onParseStart(line);
					elementComment.append(line);
					element = optionalElementParser.get().getResult();
				} else {
					logger.error("Could not parse line:{}", line);
				}
				isFoundAt = true;
			}

		}
		if (parseResult.endIndex == -1) {
			parseResult.endIndex = lines.size();
		}
		if(element!=null) {
		    parseResult.element = element;
		}
		if (parseResult.element != null) {
			parseResult.element.comment = elementComment.toString();
		}
		return parseResult;
	}

}
