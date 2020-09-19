package com.hngd.model;

import java.util.Date;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *角色
 */
public class Role {
    /**
     *角色ID
     */
    @Size(min=0,max=32)
    private String id;

    /**
     *角色名称
     */
    @Size(min=0,max=40)
    private String name;

    /**
     *备注
     */
    @Size(min=0,max=256)
    private String remark;

    /**
     *是否禁用(1:启用,0:禁用)
     */
    private Short state;

    /**
     *是否系统内置角色(0:不是,1:是)
     */
    private Short inbuilt;

    /**
     *null
     */
    @Size(min=0,max=100)
    private String createdBy;

    /**
     *null
     */
    private Date creationTime;

    /**
     *null
     */
    @Size(min=0,max=100)
    private String modifiedBy;

    /**
     *null
     */
    private Date modificationTime;

    /**
     *null
     */
    @Size(min=0,max=1)
    private String deleteStatus;

    /**
     *角色编码，不是必须的，有角色权限的部分可以使用
     */
    @Size(min=0,max=50)
    private String code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public Short getInbuilt() {
        return inbuilt;
    }

    public void setInbuilt(Short inbuilt) {
        this.inbuilt = inbuilt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy == null ? null : createdBy.trim();
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy == null ? null : modifiedBy.trim();
    }

    public Date getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus == null ? null : deleteStatus.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }
}