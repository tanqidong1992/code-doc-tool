/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：TestClassCommentParser.java
 * @时间：2017年2月14日 下午5:24:57
 * @作者：
 * @备注：
 * @版本:
 */

package com.hngd.parser.source;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.common.web.result.RestResponse;
import com.hngd.model.Role;
import com.hngd.parser.entity.ClassInfo;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.javadoc.BlockTag;
import com.hngd.parser.javadoc.MainDescription;
import com.hngd.parser.javadoc.JavaDocCommentElement;
import com.hngd.parser.javadoc.JavaDocCommentParser;

/**
 * @author
 */
public class ClassCommentParseTest{
 
    @Test
    public void main() throws URISyntaxException, IOException{
        Path path = new File("./test-data/class-comment.txt").toPath();
        List<String> commentLines = Files.readAllLines(path);
        List<JavaDocCommentElement>  ces= JavaDocCommentParser.parse(commentLines);
        ces.stream()
            .filter(MainDescription.class::isInstance)
            .map(MainDescription.class::cast)
            .findFirst()
            .ifPresent(desc->{
                Assert.assertEquals("设备状态", desc.getContent());
            });
        
        ces.stream()
            .filter(BlockTag.class::isInstance)
            .map(BlockTag.class::cast)
            .forEach(ce ->{
                Assert.assertTrue(StringUtils.isNotEmpty(ce.getContent()));
                System.out.println(ce+"-->"+ce.getKeyword() + "-->" + ce.getContent());
            });
    }
    

    @Test
    public void testClassComment(){
        File f=new File("./src/test/java/com/hngd/parser/source/ClassCommentParseTest.java");
        SourceParserContext pc=new SourceParserContext();
        SourceParseResult parseResult=pc.doParseSourceFile(f);
        CommentStore cs=pc.getCommentStore();
        cs.save(parseResult);
        cs.print();
        Assert.assertEquals("comment for class a", 
           cs.getClassComment(A.class).getComment());
        Assert.assertEquals("comment for class b", 
           cs.getClassComment(B.class).getComment());
        Assert.assertEquals("comment for class c", 
           cs.getClassComment(C.class).getComment());
        Assert.assertEquals("comment for class d", 
           cs.getClassComment(D.class).getComment());
    }

    /**
     * comment for class a
     */
    static class A{

    }

    /** comment for class b
     */
    static class B{

    }

    /** 
     * comment for class c */
    static class C{

    }

    /**comment for class d */
    static class D{

    }

    @Test
    public void testParse(){
        
        File f=new File("./src/test/java/com/hngd/parser/source/ClassCommentParseTest.java");
        SourceParserContext pc=new SourceParserContext();
        SourceParseResult parseResult=pc.doParseSourceFile(f);
        CommentStore cs=pc.getCommentStore();
        cs.save(parseResult);
        cs.print();
        Class<?> clazz=RoleController.class;
        ClassInfo c=cs.getClassComment(clazz);
        Assert.assertTrue(!StringUtils.isEmpty(c.getComment()));
        Method[] methods=clazz.getDeclaredMethods();
        for(int i=0;i<methods.length;i++) {
            Method method=methods[i];
            if(!method.getName().equals("getPagedRoles")) {
                continue;
            }
            Optional<MethodInfo> mi=cs.getMethodInfo(method);
            Assert.assertTrue(StringUtils.isNotEmpty(mi.get().getComment()));
            mi.get().getParameters()
            .forEach(pi->{
                Assert.assertTrue(StringUtils.isNotEmpty(pi.getComment()));
            });
        }
    }
    
    
    /**
     * 系统角色管理
     * 
     * @author tqd
     */
    @RestController
    @RequestMapping("/role")
    @Validated
    public class RoleController {
        /**
         * 分页加载系统角色列表
         * @param pageNo 页号
         * @param pageSize 分页大小
         * @param roleName 角色名称,可选查询条件,模糊查询
         * @return
         * @author tqd
         * @since 0.0.1
         * @time 2018年7月13日 下午3:26:45
         */
        @GetMapping("/paged/list")
        public RestResponse<List<Role>> getPagedRoles(
                @Min(1)@RequestParam("pageNo")Integer pageNo,
                @Min(1)@RequestParam("pageSize")Integer pageSize,
                @RequestParam(value="roleName",required=false)String roleName) {
            
            return null;
        }
    }
}
