package com.hngd.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Teacher extends Person {

	/**
	 * 年龄
	 */
	private Long age;
}
