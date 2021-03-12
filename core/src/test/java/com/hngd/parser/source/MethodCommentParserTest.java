package com.hngd.parser.source;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.javadoc.BlockTag.ParamBlockTag;
import com.hngd.parser.javadoc.BlockTag.ReturnBlockTag;
import com.hngd.parser.javadoc.MainDescription;
import com.hngd.parser.javadoc.JavaDocCommentElement;
import com.hngd.parser.javadoc.JavaDocCommentParser;
import com.hngd.parser.javadoc.extension.DescriptionBlockTag;
import com.hngd.parser.javadoc.extension.SummaryBlockTag;

public class MethodCommentParserTest {

    static class A{
        
        /**
         * c for test
         * @param a c for a
         * @param b c for b*/
        public void test(String a,String b) {}
        /**c for
         * test1
         * @param a c for a
         * @param b c for b*/
        public void test1(String a,String b) {}
        /**c for test2*/
        public void test2() {}
    }
    @Test
    public void testMethodComment() {
        String path="src/test/java/com/hngd/parser/source/MethodCommentParserTest.java";
        SourceParserContext pc=new SourceParserContext();
        SourceParseResult parseResult=pc.doParseSourceFile(new File(path));
        CommentStore cs=pc.getCommentStore();
        cs.save(parseResult);
        cs.print();
        Method test=ReflectionUtils.findMethod(A.class, "test",String.class,String.class);
        Method test1=ReflectionUtils.findMethod(A.class, "test1",String.class,String.class);
        Method test2=ReflectionUtils.findMethod(A.class, "test2");
        MethodInfo m=cs.getMethodInfo(test).get();
        Assert.assertEquals("c for test", m.getComment());
        Assert.assertEquals("c for a", m.getParameters().get(0).getComment());
        Assert.assertEquals("c for b", m.getParameters().get(1).getComment());
        MethodInfo m1=cs.getMethodInfo(test1).get();
        Assert.assertEquals("c fortest1", m1.getComment());
        Assert.assertEquals("c for a", m1.getParameters().get(0).getComment());
        Assert.assertEquals("c for b", m1.getParameters().get(1).getComment());
        MethodInfo m2=cs.getMethodInfo(test2).get();
        Assert.assertEquals("c for test2", m2.getComment());
    }
    
    @Test
    public void test() throws URISyntaxException, IOException {
        Path path = new File("./test-data/method-comment.txt").toPath();
        List<String> commentLines = Files.readAllLines(path);
            
        List<JavaDocCommentElement> jdc=JavaDocCommentParser.parse(commentLines);
        jdc.forEach(i->System.out.println(i.getClass().getCanonicalName()));
        
        SummaryBlockTag summaryBlock=findElement(jdc, SummaryBlockTag.class).get();
        Assert.assertTrue(summaryBlock.getContent().equals("summary for deleteTeacher"));
        
        DescriptionBlockTag descriptionBlock=findElement(jdc, DescriptionBlockTag.class).get();
        Assert.assertTrue(descriptionBlock.getContent().equals("description for deleteTeacher"));

        ParamBlockTag paramBlock=findElement(jdc, ParamBlockTag.class).get();
        Assert.assertTrue(paramBlock.getContent().equals("待删除老师的ID"));
        
        MainDescription description=findElement(jdc, MainDescription.class).get();
        Assert.assertTrue(description.getContent().equals("删除老师信息"));
        
        
        ReturnBlockTag returnBlock=findElement(jdc, ReturnBlockTag.class).get();
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
