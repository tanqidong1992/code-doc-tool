
package com.hngd.parser.source;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hngd.parser.javadoc.BlockTag;
import com.hngd.parser.javadoc.BlockTag.ParamBlockTag;
import com.hngd.parser.javadoc.MainDescription;
import com.hngd.parser.javadoc.JavaDocCommentElement;
import com.hngd.parser.javadoc.JavaDocCommentParser;

/*
 * 
 * @author tqd
 */
public class MultiLineParamJavaDocCommentParserTest{

    @Test
    public void main() throws URISyntaxException, IOException{
        
        Path path = new File("./test-data/multi-line-block.txt").toPath();
        List<String> lines = Files.readAllLines(path);
        List<JavaDocCommentElement> pce = new ArrayList<>();
        pce = JavaDocCommentParser.parse(lines);
        pce.stream()
          .filter(MainDescription.class::isInstance)
          .map(MainDescription.class::cast)
          .findFirst()
          .ifPresent(description->{
              System.out.println("java doc comment description:"+description.getContent());
          });
        pce.stream()
        .filter(BlockTag.class::isInstance)
        .map(BlockTag.class::cast)
        .forEach(ce ->{
            if(ce instanceof ParamBlockTag) {
                System.out.println(ce.getTag() + "-->"+((ParamBlockTag)ce).getParamName()+"-->" + ce.getContent());
            }else {
                System.out.println(ce.getTag() + "-->" + ce.getContent());
            }
            
        });
    }

    
}
