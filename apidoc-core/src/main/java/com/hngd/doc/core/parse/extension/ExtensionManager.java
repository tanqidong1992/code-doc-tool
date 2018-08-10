/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：ExtensionManager.java
 * @时间：2017年2月14日 下午4:39:46
 * @作者：
 * @备注：
 * @版本:
 */

package com.hngd.doc.core.parse.extension;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.doc.core.parse.CommentElement;

/**
 * @author
 */
public class ExtensionManager
{
    private static final Logger logger = LoggerFactory.getLogger(ExtensionManager.class);

    public static void enableExtension(Class<? extends CommentElement> clazz)
    {
        try
        {
            clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e)
        {
            logger.error("", e);
        }
    }
}
