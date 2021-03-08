package com.hngd.test.dto;

import java.util.Date;


import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

public class FormWithJacksonDate {
 
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date date;
    private String name;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
