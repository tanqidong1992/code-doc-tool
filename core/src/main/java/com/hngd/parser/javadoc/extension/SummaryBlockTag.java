package com.hngd.parser.javadoc.extension;

import com.hngd.parser.javadoc.BlockTag;

/**
 * https://swagger.io/specification/#pathItemObject summary
 * @author tqd
 */
public class SummaryBlockTag extends BlockTag
{
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @author tqd
     * @since 0.0.1 
     */
    public SummaryBlockTag()
    {
        super("@summary");
    }
}
