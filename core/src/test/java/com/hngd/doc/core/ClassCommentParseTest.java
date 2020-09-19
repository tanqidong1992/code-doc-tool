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

package com.hngd.doc.core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.hngd.parser.javadoc.BlockTag;
import com.hngd.parser.javadoc.Description;
import com.hngd.parser.javadoc.JavaDocCommentElement;
import com.hngd.parser.javadoc.JavaDocCommentParser;
import com.hngd.parser.javadoc.extension.ExtensionManager;
import com.hngd.parser.javadoc.extension.MobileBlock;
import com.hngd.parser.javadoc.extension.TimeBlock;

/**
 * @author
 */
public class ClassCommentParseTest
{
 
	@Test
    public void main() throws URISyntaxException, IOException
    {
        URL url = ClassCommentParseTest.class.getResource("classcomment.txtx");
        Path path = Paths.get(url.toURI());
    	List<String> commentLines = Files.readAllLines(path);
        List<JavaDocCommentElement>  ces= JavaDocCommentParser.parse(commentLines);
        ces.stream()
        .filter(Description.class::isInstance)
        .map(Description.class::cast)
        .findFirst()
        .ifPresent(desc->{
        	Assert.assertEquals("设备状态", desc.getContent());
        });
        
        ces.stream()
        .filter(BlockTag.class::isInstance)
        .map(BlockTag.class::cast)
        .forEach(ce ->{
        	Assert.assertTrue(StringUtils.isNotEmpty(ce.getContent()));
            System.out.println(ce+"-->"+ce.getTag() + "-->" + ce.getContent());
        });
    }
}
