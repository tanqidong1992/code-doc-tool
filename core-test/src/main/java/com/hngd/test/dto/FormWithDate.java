package com.hngd.test.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;


import lombok.Data;

@Data
public class FormWithDate {
 
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date date;
	private String name;
}
