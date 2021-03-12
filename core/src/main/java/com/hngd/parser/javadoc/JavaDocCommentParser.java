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
 * Java文档注释解析类
 * <a href="https://docs.oracle.com/javase/1.5.0/docs/tooldocs/windows/javadoc.html#documentationcomments">documentationcomments</a>
 * @author tqd
 * @version 0.0.1
 */
public class JavaDocCommentParser {

    private static final Logger logger = LoggerFactory.getLogger(JavaDocCommentParser.class);

    private static class ParseResult {
        public static int UNSET_INDEX_VALUE=-1;
        @SuppressWarnings("unused")
        int startIndex = UNSET_INDEX_VALUE;
        int endIndex = UNSET_INDEX_VALUE;
        JavaDocCommentElement element;
        public boolean hasResult() {
            return element!=null;
        }
    }

    public static List<JavaDocCommentElement> parse(List<String> commentLines) {
        if (commentLines==null || commentLines.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> lines = preprocess(commentLines);
        List<JavaDocCommentElement> commentElements = new ArrayList<>();
        ParseResult result = parseMainDescription(lines);
        if(result.hasResult()) {
            commentElements.add(result.element);
        }
        //parse tag section
        while (result.endIndex < lines.size()) {
            result = parseElement(lines, result.endIndex);
            if(result.hasResult()) {
                commentElements.add(result.element);
            }
        }
        return commentElements;
    }
    /**
     * remove start and end of javadoc comment
     * @param commentLines
     * @return
     */
    private static List<String> preprocess(List<String> commentLines) {
        if(commentLines==null || commentLines.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> processedLines=new ArrayList<>(commentLines.size());
        if(commentLines.size()==1) {
            String line=commentLines.get(0);
            line=line.trim().replace("/**", "").replace("*/", "").trim();
            if(line.length()>0) {
                processedLines.add(line);
            }
            return processedLines;
        }else {
            String firstLine=commentLines.get(0);
            firstLine=firstLine.trim().replace("/**", "").trim();
            if(firstLine.length()>0) {
                processedLines.add(firstLine);
            }
            for(int i=1;i<commentLines.size()-1;i++) {
                processedLines.add(commentLines.get(i));
            }
            String lastLine=commentLines.get(commentLines.size()-1);
            lastLine=lastLine.trim().replace("*/", "").trim();
            if(lastLine.length()>0) {
                processedLines.add(lastLine);
            }
        }
        return processedLines;
    }

    private static ParseResult parseMainDescription(List<String> lines) {
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
        MainDescription description = new MainDescription();
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
                Optional<BlockTagParseListener> blockTagParserListener =
                    BlockTagParserContext.findElementParser(line);
                if (blockTagParserListener.isPresent()) {
                    line = blockTagParserListener.get().onParseStart(line);
                    content.append(line);
                    element = blockTagParserListener.get().getResult();
                } else {
                    logger.warn("Could not parse line:{}", line);
                }
                isFoundAt = true;
            }
        }
        if (parseResult.endIndex == -1) {
            parseResult.endIndex = lines.size();
        }
        if(element!=null) {
            parseResult.element = element;
            parseResult.element.setContent(content.toString());
        }
        return parseResult;
    }
}
