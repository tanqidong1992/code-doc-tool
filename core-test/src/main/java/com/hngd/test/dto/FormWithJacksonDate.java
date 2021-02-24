package com.hngd.test.dto;

import java.util.Date;


import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class FormWithJacksonDate {
 
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date date;
    private String name;
}
