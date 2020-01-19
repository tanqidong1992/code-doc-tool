package com.hngd.doc.core.plugin;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.hngd.parser.javadoc.extension.TimeBlock;


public class TimeElementTest {

	@Test
	public void test() {
		String timeStr = "2017年3月15日 下午16:30:06";
		Date d = TimeBlock.parseTime(timeStr);
		Assert.assertEquals(d.getSeconds(), 6);
	}
}
