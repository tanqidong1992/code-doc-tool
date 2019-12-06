package com.hngd.parser.source;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.hngd.parser.source.CommentElement.AuthorElement;
import com.hngd.parser.source.CommentElement.ParamElement;
import com.hngd.parser.source.CommentElement.ReturnElement;
import com.hngd.parser.source.CommentElement.SeeElement;
import com.hngd.parser.source.CommentElement.SinceElement;
import com.hngd.parser.source.CommentElement.ThrowsElement;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class CommentElementParserContext {

	private static Map<String, Class<? extends CommentElementParser>> commentElementParsers = new HashMap<>();
	static {
		// register internal parse type
		new ParamElement();
		new AuthorElement();
		new ReturnElement();
		new SeeElement();
		new SinceElement();
		new ThrowsElement();
	}
	
	public static synchronized void register(String parserName,Class<? extends CommentElementParser> parserType) {
		
		if (!commentElementParsers.containsKey(parserName)) {
			commentElementParsers.put(parserName, parserType);
		}
	}
	
	
	public static Optional<CommentElementParser> findElementParser(String line) {
		Set<String> parserNames = commentElementParsers.keySet();
		Optional<CommentElementParser> optionalElementParser=parserNames.stream()
		  .filter(parserName->line.startsWith(parserName))
		  .map(CommentElementParserContext::newParser)
		  .filter(parser->parser!=null)
		  .findFirst();
	    return optionalElementParser;
	}
	
	private static CommentElementParser newParser(String parserName) {
		if(StringUtils.isEmpty(parserName)) {
			return null;
		}
		Class<? extends CommentElementParser> parserType = commentElementParsers.get(parserName);
		CommentElementParser parser=null;
		try {
			parser = parserType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("", e);
		}
		return parser;
	}
}
