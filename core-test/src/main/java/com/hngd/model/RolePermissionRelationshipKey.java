package com.hngd.model;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *角色权限关联
 */
public class RolePermissionRelationshipKey {
    /**
     *角色ID
     */
    @Size(min=0,max=32)
    private String roleId;

    /**
     *权限ID
     */
    @NotNull
    @Size(min=0,max=32)
    private String permissionId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId == null ? null : permissionId.trim();
    }
}