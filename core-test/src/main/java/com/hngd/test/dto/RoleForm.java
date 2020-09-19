package com.hngd.test.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 角色表单参数
 * @author tqd
 *
 */
@Data
public class RoleForm {
	/**
	 * 角色Id
	 */
	@Size(min=32,max=32)
	private String id;
    /**
     * 角色名称
     */
	@NotNull
	@Size(max=40)
	//@Pattern
	private String name;
	/**
	 * 角色描述
	 */
	@Size(max=200)
	private String remark;
}
