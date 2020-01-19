package com.hngd.model;

import java.util.Date;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *角色权限关联
 */
public class RolePermissionRelationship extends RolePermissionRelationshipKey {
    /**
     *null
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
}