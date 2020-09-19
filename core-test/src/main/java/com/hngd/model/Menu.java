package com.hngd.model;

import java.util.Date;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *菜单
 */
public class Menu {
    /**
     *菜单ID
     */
    @Size(min=0,max=32)
    private String id;

    /**
     *菜单显示名称
     */
    @NotNull
    @Size(min=0,max=128)
    private String name;

    /**
     *上级菜单ID,根菜单的上级ID为-1
     */
    @NotNull
    @Size(min=0,max=32)
    private String parentId;

    /**
     *备注
     */
    @Size(min=0,max=256)
    private String remark;

    /**
     *同级菜单展示序号
     */
    @NotNull
    private Short orderNo;

    /**
     *菜单对应页面ID
     */
    @Size(min=0,max=32)
    private String uiPageId;

    /**
     *关联权限ID
     */
    @Size(min=0,max=32)
    private String permissionId;

    /**
     *菜单图标
     */
    @Size(min=0,max=512)
    private String icon;

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
     *权限编码
     */
    @NotNull
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Short getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Short orderNo) {
        this.orderNo = orderNo;
    }

    public String getUiPageId() {
        return uiPageId;
    }

    public void setUiPageId(String uiPageId) {
        this.uiPageId = uiPageId == null ? null : uiPageId.trim();
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId == null ? null : permissionId.trim();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
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