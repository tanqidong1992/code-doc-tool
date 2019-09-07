/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：TimeElement.java
 * @时间：2017年2月14日 下午4:34:11
 * @作者：
 * @备注：
 * @版本:
 */

package com.hngd.doc.core.parse.extension;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.doc.core.parse.CommentElement;

/**
 * @author
 */
public class TimeElement extends CommentElement {
	private static final Logger logger = LoggerFactory.getLogger(TimeElement.class);
	public Date createTime;
	public String createTimeStr;

	/**
	 * @author
	 * @since 0.0.1
	 */
	public TimeElement() {
		super("@time");
	}

	@Override
	public String parse(String line) {
		line = super.parse(line);
		createTime = parseTime(line);
		if (createTime != null) {
			createTimeStr = sdf.format(createTime).split(" ")[0];
		} else {
			createTimeStr = null;
		}
		return createTimeStr;
	}

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

	public static Date parseTime(String timeStr) {
		String source = timeStr;
		Date date = null;
		if (source.contains("下午")) {
			source = source.replace("下午", "");
			try {
				date = sdf.parse(source);
			} catch (java.text.ParseException e) {
				logger.error("", e);
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.HOUR_OF_DAY, 12);
			date = calendar.getTime();
		} else if(source.contains("上午")){
			source = source.replace("上午", "");
			try {
				date = sdf.parse(source);
			} catch (java.text.ParseException e) {
				logger.error("", e);
			}
		}
		return date;
	}

	
}
