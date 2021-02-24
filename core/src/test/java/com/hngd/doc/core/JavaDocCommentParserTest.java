
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
import com.hngd.parser.javadoc.Description;
import com.hngd.parser.javadoc.JavaDocCommentElement;
import com.hngd.parser.javadoc.JavaDocCommentParser;

/*
 * 
 * @author tqd
 */
public class JavaDocCommentParserTest
{

    @Test
    public void main() throws URISyntaxException, IOException
    {
        URL url = JavaDocCommentParserTest.class.getResource("comment.txt");
        Path path = Paths.get(url.toURI());
        List<String> lines = Files.readAllLines(path);
        List<JavaDocCommentElement> pce = new ArrayList<>();
        pce = JavaDocCommentParser.parse(lines);
        pce.stream()
          .filter(Description.class::isInstance)
          .map(Description.class::cast)
          .findFirst()
          .ifPresent(description->{
              Assert.assertTrue("处理告警信息".equals(description.getContent()));
              System.out.println("java doc comment description:"+description.getContent());
          });
        pce.stream()
        .filter(BlockTag.class::isInstance)
        .map(BlockTag.class::cast)
        .forEach(ce ->{
            System.out.println(ce.getTag() + "-->" + ce.getContent());
            Assert.assertTrue(StringUtils.isNotEmpty(ce.getContent()));
        });
    }

    
}
