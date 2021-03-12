package com.hngd.parser.javadoc.extension;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class TimeBlockTagTest {

    @SuppressWarnings("deprecation")
    @Test
    public void test() {
        String timeStr = "2017年3月15日 下午16:30:06";
        Date d = TimeBlockTag.parseTime(timeStr);
        Assert.assertEquals(d.getSeconds(), 6);
    }
}
