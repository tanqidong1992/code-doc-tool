package com.hngd.parser.javadoc.extension;

import com.hngd.parser.javadoc.BlockTag;

/**
 * https://swagger.io/specification/#pathItemObject description
 * @author tqd
 */
public class DescriptionBlockTag extends BlockTag
{
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @author tqd
     * @since 0.0.1 
     */
    public DescriptionBlockTag()
    {
        super("@description");
    }
}
