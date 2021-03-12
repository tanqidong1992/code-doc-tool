package com.hngd.parser.javadoc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.hngd.parser.javadoc.BlockTag.AuthorBlockTag;
import com.hngd.parser.javadoc.BlockTag.ParamBlockTag;
import com.hngd.parser.javadoc.BlockTag.ReturnBlockTag;
import com.hngd.parser.javadoc.BlockTag.SeeBlockTag;
import com.hngd.parser.javadoc.BlockTag.SinceBlockTag;
import com.hngd.parser.javadoc.BlockTag.ThrowsBlockTag;
import com.hngd.parser.javadoc.extension.DescriptionBlockTag;
import com.hngd.parser.javadoc.extension.SummaryBlockTag;
import com.hngd.parser.javadoc.extension.TagsBlockTag;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockTagParserContext {

    private static Map<String, Class<? extends BlockTagParseListener>> blockTagParsers = new HashMap<>();
    static {
        // register internal parse type
        new ParamBlockTag();
        new AuthorBlockTag();
        new ReturnBlockTag();
        new SeeBlockTag();
        new SinceBlockTag();
        new ThrowsBlockTag();
        new SummaryBlockTag();
        new DescriptionBlockTag();
        new TagsBlockTag();
    }
    
    public static synchronized void register(String parserName,Class<? extends BlockTagParseListener> parserType) {
        
        if (!blockTagParsers.containsKey(parserName)) {
            blockTagParsers.put(parserName, parserType);
        }
    }
    
    
    public static Optional<BlockTagParseListener> findElementParser(String line) {
        Set<String> parserNames = blockTagParsers.keySet();
        Optional<BlockTagParseListener> blockTagParser=parserNames.stream()
          .filter(parserName->line.startsWith(parserName))
          .map(BlockTagParserContext::newParser)
          .filter(parser->parser!=null)
          .findFirst();
        return blockTagParser;
    }
    
    private static BlockTagParseListener newParser(String parserName) {
        if(StringUtils.isEmpty(parserName)) {
            return null;
        }
        Class<? extends BlockTagParseListener> parserType = blockTagParsers.get(parserName);
        BlockTagParseListener parser=null;
        try {
            parser = parserType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("", e);
        }
        return parser;
    }
}
