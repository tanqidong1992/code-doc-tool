package com.hngd.parser.source;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.hngd.parser.javadoc.BlockTag.ParamBlock;
import com.hngd.parser.javadoc.BlockTag.ReturnBlock;
import com.hngd.parser.javadoc.Description;
import com.hngd.parser.javadoc.JavaDocCommentElement;
import com.hngd.parser.javadoc.JavaDocCommentParser;
import com.hngd.parser.javadoc.extension.DescriptionBlock;
import com.hngd.parser.javadoc.extension.SummaryBlock;

public class MethodCommentParserTest {

    @Test
    public void test() throws URISyntaxException, IOException {
        URL url = MethodCommentParserTest.class.getResource("method-comment.txt");
        Path path = Paths.get(url.toURI());
        List<String> commentLines = Files.readAllLines(path);
            
        List<JavaDocCommentElement> jdc=JavaDocCommentParser.parse(commentLines);
        jdc.forEach(i->System.out.println(i.getClass().getCanonicalName()));
        
        SummaryBlock summaryBlock=findElement(jdc, SummaryBlock.class).get();
        Assert.assertTrue(summaryBlock.getContent().equals("summary for deleteTeacher"));
        
        DescriptionBlock descriptionBlock=findElement(jdc, DescriptionBlock.class).get();
        Assert.assertTrue(descriptionBlock.getContent().equals("description for deleteTeacher"));

        ParamBlock paramBlock=findElement(jdc, ParamBlock.class).get();
        Assert.assertTrue(paramBlock.getContent().equals("待删除老师的ID"));
        
        Description description=findElement(jdc, Description.class).get();
        Assert.assertTrue(description.getContent().equals("删除老师信息"));
        
        
        ReturnBlock returnBlock=findElement(jdc, ReturnBlock.class).get();
        Assert.assertTrue(returnBlock.getContent().equals("return comment"));
    }
    
    public static <T> Optional<T> findElement(List<JavaDocCommentElement> elements,Class<T> type) {
        Optional<T> element=elements.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
        return element;
    } 

}
