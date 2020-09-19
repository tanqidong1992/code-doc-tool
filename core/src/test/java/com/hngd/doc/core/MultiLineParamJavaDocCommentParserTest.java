
package com.hngd.doc.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.hngd.parser.javadoc.BlockTag;
import com.hngd.parser.javadoc.BlockTag.ParamBlock;
import com.hngd.parser.javadoc.Description;
import com.hngd.parser.javadoc.JavaDocCommentElement;
import com.hngd.parser.javadoc.JavaDocCommentParser;

/*
 * 
 * @author tqd
 */
public class MultiLineParamJavaDocCommentParserTest
{

	@Test
    public void main() throws URISyntaxException, IOException
    {
        URL url = MultiLineParamJavaDocCommentParserTest.class.getResource("multili-block.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path);
        List<JavaDocCommentElement> pce = new ArrayList<>();
        pce = JavaDocCommentParser.parse(lines);
        pce.stream()
          .filter(Description.class::isInstance)
          .map(Description.class::cast)
          .findFirst()
          .ifPresent(description->{
        	  System.out.println("java doc comment description:"+description.getContent());
          });
        pce.stream()
        .filter(BlockTag.class::isInstance)
        .map(BlockTag.class::cast)
        .forEach(ce ->{
        	if(ce instanceof ParamBlock) {
        		System.out.println(ce.getTag() + "-->"+((ParamBlock)ce).getParamName()+"-->" + ce.getContent());
        	}else {
        		System.out.println(ce.getTag() + "-->" + ce.getContent());
        	}
            
        });
    }

    
}
