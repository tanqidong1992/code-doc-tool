package com.hngd.test.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
/**
 * 人员
 * @author tqd
 *
 */
@Data
public class Person {

	
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 性别
	 */
	@JsonProperty("gender1")
	private String gender;
}
