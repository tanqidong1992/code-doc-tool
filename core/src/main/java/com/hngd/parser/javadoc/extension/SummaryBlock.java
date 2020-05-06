/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：MobileElement.java
 * @时间：2017年2月14日 下午5:14:03
 * @作者：
 * @备注：
 * @版本:
 */
package com.hngd.parser.javadoc.extension;

import com.hngd.parser.javadoc.BlockTag;

/**
 * https://swagger.io/specification/#pathItemObject summary
 * @author tqd
 */
public class SummaryBlock extends BlockTag
{
    /**
     * 
     * @author tqd
     * @since 0.0.1 
     */
    public SummaryBlock()
    {
    	super("@summary");
    }
}