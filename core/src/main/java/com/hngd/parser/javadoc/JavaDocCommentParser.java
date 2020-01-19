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

package com.hngd.parser.javadoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java文档注释解析类,用于解析javadoc comment
 * 参考 <a href="https://www.oracle.com/technetwork/java/javase/documentation/index-137868.html">How to Write Doc Comments for the Javadoc Tool</a>
 * @author tqd
 * @version 0.0.1
 */
public class JavaDocCommentParser {
	/**
	 * java doc comment must has two lines
	 */
	public static final int JAVA_DOC_COMMENT_MIN_LINE_SIZE=2;
	private static final Logger logger = LoggerFactory.getLogger(JavaDocCommentParser.class);

	private static class ParseResult {
		public static int UNSET_INDEX_VALUE=-1;
		int startIndex = UNSET_INDEX_VALUE;
		int endIndex = UNSET_INDEX_VALUE;
		JavaDocCommentElement element;
		public boolean hasResult() {
			return element!=null;
		}
	}

	public static List<JavaDocCommentElement> parse(List<String> commentLines) {
		if (commentLines==null || commentLines.size() <= JAVA_DOC_COMMENT_MIN_LINE_SIZE) {
			return Collections.emptyList();
		}
		//remove the first line and last line
		List<String> lines = commentLines.subList(1, commentLines.size() - 1);
		List<JavaDocCommentElement> commentElements = new ArrayList<>();
		//parse description
		ParseResult result = parseDescription(lines);
		if(result.hasResult()) {
			commentElements.add(result.element);
		}
		//parse block tags
		while (result.endIndex < lines.size()) {
			result = parseElement(lines, result.endIndex);
			if(result.hasResult()) {
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
		Description description = new Description();
		description.setContent(desc.toString());
		result.element = description;
		if (result.endIndex == ParseResult.UNSET_INDEX_VALUE) {
			result.endIndex = lines.size();
		}
		return result;
	}

	private static ParseResult parseElement(List<String> lines, int startIndex) {
		BlockTag element = null;
		ParseResult parseResult = new ParseResult();
		parseResult.startIndex = startIndex;
		StringBuilder content = new StringBuilder();
		boolean isFoundAt = false;
		for (int i = startIndex; i < lines.size(); i++) {
			if (StringUtils.isEmpty(lines.get(i))) {
				continue;
			}
			String line = lines.get(i).replaceFirst("\\*", "").trim();
			if(!line.startsWith("@")) {
				content.append(line);
				continue;
			}
			if (isFoundAt) {
				parseResult.endIndex = i;
				break;
			} else {
				Optional<JavaDocCommentBlockTagParser> optionalElementParser = CommentBlockParserContext.findElementParser(line);
				if (optionalElementParser.isPresent()) {
					line = optionalElementParser.get().onParseStart(line);
					content.append(line);
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
			parseResult.element.setContent(content.toString());
		}
		return parseResult;
	}

}
