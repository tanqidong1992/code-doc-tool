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

package com.hngd.doc.core.parse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author
 */
public class ClassCommentParser {
	private static final Logger logger = Logger.getLogger(ClassCommentParser.class);

	static class ParseResult {
		int startIndex;
		int endIndex = -1;
		CommentElement elemnet;
	}

	public static List<CommentElement> parseMethodComment(List<String> commentLines) {
		if (commentLines.size() <= 2) {
			return null;
		}
		List<String> lines = commentLines.subList(1, commentLines.size() - 1);
		List<CommentElement> pce = new ArrayList<>();
		ParseResult pr = parseDesc(lines);
		pce.add(pr.elemnet);
		while (pr.endIndex < lines.size()) {
			if (pr.endIndex == -1) {
				System.out.println("test");
			}
			pr = parseElement(lines, pr.endIndex);
			pce.add(pr.elemnet);
		}
		return pce;
	}

	private static ParseResult parseDesc(List<String> lines) {
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
			desc.append(line).append(" ");
		}
		CommentElement ce = new CommentElement.DescElement();
		ce.comment = desc.toString();
		pr.elemnet = ce;
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
		boolean isFountAt = false;
		for (int i = startIndex; i < lines.size(); i++) {
			if (StringUtils.isEmpty(lines.get(i))) {
				continue;
			}
			String line = lines.get(i).replaceFirst("\\*", "").trim();
			if (line.startsWith("@")) {
				if (isFountAt) {
					pr.endIndex = i;
					break;
				} else {
					Set<String> keys = CommentElement.commentElements.keySet();
					for (String key : keys) {
						if (line.startsWith(key)) {
							CommentElement ceType = CommentElement.commentElements.get(key);
							try {
								ce = ceType.getClass().newInstance();
							} catch (InstantiationException | IllegalAccessException e) {
								logger.error("", e);
							}

							if (ce != null) {
								line = ce.parse(line);
							} else {
								logger.error("could not parse line[" + line + "]");
							}
							break;
						}
					}
					if (ce == null) {
						logger.error("could not parse line[" + line + "]");
					}
					isFountAt = true;
				}
			}
			desc.append(line).append(" ");
		}
		if (pr.endIndex == -1) {
			pr.endIndex = lines.size();
		}
		pr.elemnet = ce;
		if (ce != null) {
			ce.comment = desc.toString();
		}
		return pr;
	}
}
