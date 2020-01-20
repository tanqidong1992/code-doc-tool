package com.hngd.parser.javadoc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.hngd.parser.javadoc.BlockTag.AuthorBlock;
import com.hngd.parser.javadoc.BlockTag.ParamBlock;
import com.hngd.parser.javadoc.BlockTag.ReturnBlock;
import com.hngd.parser.javadoc.BlockTag.SeeBlock;
import com.hngd.parser.javadoc.BlockTag.SinceBlock;
import com.hngd.parser.javadoc.BlockTag.ThrowsBlock;
import com.hngd.parser.javadoc.extension.DescriptionBlock;
import com.hngd.parser.javadoc.extension.SummaryBlock;
import com.hngd.parser.javadoc.extension.TagsBlock;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class CommentBlockParserContext {

	private static Map<String, Class<? extends JavaDocCommentBlockTagParser>> commentElementParsers = new HashMap<>();
	static {
		// register internal parse type
		new ParamBlock();
		new AuthorBlock();
		new ReturnBlock();
		new SeeBlock();
		new SinceBlock();
		new ThrowsBlock();
		new SummaryBlock();
		new DescriptionBlock();
		new TagsBlock();
	}
	
	public static synchronized void register(String parserName,Class<? extends JavaDocCommentBlockTagParser> parserType) {
		
		if (!commentElementParsers.containsKey(parserName)) {
			commentElementParsers.put(parserName, parserType);
		}
	}
	
	
	public static Optional<JavaDocCommentBlockTagParser> findElementParser(String line) {
		Set<String> parserNames = commentElementParsers.keySet();
		Optional<JavaDocCommentBlockTagParser> optionalElementParser=parserNames.stream()
		  .filter(parserName->line.startsWith(parserName))
		  .map(CommentBlockParserContext::newParser)
		  .filter(parser->parser!=null)
		  .findFirst();
	    return optionalElementParser;
	}
	
	@SuppressWarnings("deprecation")
	private static JavaDocCommentBlockTagParser newParser(String parserName) {
		if(StringUtils.isEmpty(parserName)) {
			return null;
		}
		Class<? extends JavaDocCommentBlockTagParser> parserType = commentElementParsers.get(parserName);
		JavaDocCommentBlockTagParser parser=null;
		try {
			parser = parserType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("", e);
		}
		return parser;
	}
}
