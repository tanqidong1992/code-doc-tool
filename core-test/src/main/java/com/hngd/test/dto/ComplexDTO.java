package com.hngd.test.dto;

import java.util.List;

import lombok.Data;

@Data
public class ComplexDTO {

	/**
	 * 人员信息
	 */
	private Person person;
	/**
	 * 老师信息
	 */
	private Teacher teacher;
	/**
	 * 其他信息
	 */
	private List<OtherInfo> otherInfos;
}
