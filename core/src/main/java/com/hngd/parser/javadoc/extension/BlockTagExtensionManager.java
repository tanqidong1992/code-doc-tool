package com.hngd.parser.javadoc.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.parser.javadoc.BlockTag;

/**
 * @author tqd
 */
public class BlockTagExtensionManager {
    private static final Logger logger = LoggerFactory.getLogger(BlockTagExtensionManager.class);

    public static void enableExtension(Class<? extends BlockTag> clazz) {
        try {
            clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("", e);
        }
    }
}
