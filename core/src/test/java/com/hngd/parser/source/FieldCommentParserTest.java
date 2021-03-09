package com.hngd.parser.source;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.hngd.test.dto.MenuTreeNode;

public class FieldCommentParserTest {

    @Test
    public void test() {
        SourceParserContext pc=new SourceParserContext();
        String path="./src/test/java/com/hngd/test/dto/MenuTreeNode.java";
        File f=new File(path);
        pc.doParseSourceFile(f);
        pc.getCommentStore().print();
        String pageUrlComment=pc.getCommentStore()
                .getFieldComment(MenuTreeNode.class.getCanonicalName(), "pageUrl");
        Assert.assertTrue("对应页面路径".equals(pageUrlComment));
        String subMenusComment=pc.getCommentStore()
                .getFieldComment(MenuTreeNode.class.getCanonicalName(), "subMenus");
        Assert.assertTrue("子菜单项".equals(subMenusComment));
    }

}
