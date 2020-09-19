package com.hngd.model;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 */
public class Area {
    /**
     *null
     */
    @Size(min=0,max=12)
    private String code;

    /**
     *null
     */
    @Size(min=0,max=100)
    private String name;

    /**
     *null
     */
    private Short leave;

    /**
     *null
     */
    @Size(min=0,max=12)
    private String parentCode;

    /**
     *null
     */
    @Size(min=0,max=256)
    private String description;

    /**
     *null
     */
    @NotNull
    @Size(min=0,max=1)
    private String permit;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Short getLeave() {
        return leave;
    }

    public void setLeave(Short leave) {
        this.leave = leave;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode == null ? null : parentCode.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getPermit() {
        return permit;
    }

    public void setPermit(String permit) {
        this.permit = permit == null ? null : permit.trim();
    }
}