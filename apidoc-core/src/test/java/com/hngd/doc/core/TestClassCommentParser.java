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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.hngd.doc.core.parse.ClassCommentParser;
import com.hngd.doc.core.parse.CommentElement;
import com.hngd.doc.core.parse.extension.ExtensionManager;
import com.hngd.doc.core.parse.extension.MobileElement;
import com.hngd.doc.core.parse.extension.TimeElement;

/**
 * @author
 */
public class TestClassCommentParser
{
    static
    {
        ExtensionManager.enableExtension(TimeElement.class);
        ExtensionManager.enableExtension(MobileElement.class);
    }
    public static void main(String[] args) throws URISyntaxException, IOException
    {
        URL url = AppTest.class.getResource("classcomment.txtx");
        Path path = Paths.get(url.toURI());
        List<String> commentLines = Files.readAllLines(path);
        List<CommentElement>  ces= ClassCommentParser.parseMethodComment(commentLines);
        ces.forEach(ce ->
        {
            System.out.println(ce+"-->"+ce.prefix + "-->" + ce.comment);
        });
    }
}
