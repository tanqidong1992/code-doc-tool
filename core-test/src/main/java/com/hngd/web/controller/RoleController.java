package com.hngd.web.controller;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import com.hngd.common.web.auth.User;
import com.hngd.common.web.context.HttpRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.common.error.ErrorCode;
import com.hngd.common.result.Result;
import com.hngd.common.web.RestResponses;
import com.hngd.common.web.page.PagedData;
import com.hngd.common.web.result.RestResponse;
import com.hngd.model.Role;
import com.hngd.test.dto.RoleForm;


/**
 * 系统角色管理
 * 
 * @author tqd
 */
@RestController
@RequestMapping("/role")
@Validated
public class RoleController {

	 
 
    //必要的注释,尽可能详细
	/**
	 * 新增或者修改系统角色,如果role参数的id字段为空,则新增一个系统角色,否则根据Id修改已有系统角色
	 * @param role 待新增或修改的系统角色信息,
	 * @return
	 * @author tqd
	 * @since 0.0.1
	 * @time 2018年7月13日 下午3:25:48
	 */
	@PostMapping("/save")
	//如果是复杂的对象记得加@Valid注解
	public RestResponse<String> saveRole(@Valid @RequestBody RoleForm role) {
		 return null;
	}

	/**
	 * 删除系统角色
	 * @param roleId 待删除角色的Id
	 * @return
	 * @author tqd
	 * @since 0.0.1
	 * @time 2018年7月13日 下午3:26:24
	 */
	@DeleteMapping("/delete")
	public RestResponse<Void> deleteRole(@RequestParam("roleId") String roleId) {
		return null;
	}

	/**
	 * 加载系统角色列表(所有)
	 * @return
	 * @author tqd
	 * @since 0.0.1
	 * @time 2018年7月13日 下午3:26:45
	 */
	@GetMapping("/list")
	public RestResponse<List<Role>> getRolesList() {
		return null;
	}
	
	/**
	 * 分页加载系统角色列表
	 * @param pageNo 页号
	 * @param pageSize 分页大小
	 * @param roleName 角色名称,可选查询条件,模糊查询
	 * @return
	 * @author tqd
	 * @since 0.0.1
	 * @time 2018年7月13日 下午3:26:45
	 */
	@GetMapping("/paged/list")
	public RestResponse<List<Role>> getPagedRoles(
			@Min(1)@RequestParam("pageNo")Integer pageNo,
			@Min(1)@RequestParam("pageSize")Integer pageSize,
			@RequestParam(value="roleName",required=false)String roleName) {
		
		return null;
	}
	
	
	/**
	 * 授予角色权限
	 * 
	 * @param roleId
	 *            待授予角色的Id
	 * @param permissionIds
	 *            待授予权限的Id集合
	 * @return
	 * @author
	 * @since 1.0.0
	 * @time 2018年8月10日 下午1:49:06
	 */
	@PostMapping(value = "/permission/grant")
	public RestResponse<Void> grantPermission(
			  @RequestParam("roleId") String roleId,
			  @RequestParam("permissionIds") List<String> permissionIds) {
		return null;
	}

	/**
	 * 重新授予角色权限,将会覆盖原有权限
	 * 
	 * @param roleId
	 *            待重新授予目标角色的Id
	 * @param permissionIds
	 *            待重新授予权限的Id集合
	 * @return
	 * @author
	 * @since 1.0.0
	 * @time 2018年8月10日 下午1:52:31
	 */
	@PostMapping("/permission/reset")
	public RestResponse<Void> resetPermission(@RequestParam("roleId") String roleId,
			  @RequestParam("permissionIds") List<String> permissionIds) {
		return null;
	}

	/**
	 * 剥夺角色所拥有的权限
	 * 
	 * @param roleId
	 *            待剥夺目标角色的Id
	 * @param permissionIds
	 *            待剥夺权限的Id集合
	 * @return
	 * @author
	 * @since 1.0.0
	 * @time 2018年8月10日 下午1:54:39
	 */
	@PostMapping("/permission/deprive")
	public RestResponse<Void> deprivePermission(@RequestParam("roleId") String roleId,
			 @RequestParam("permissionIds") List<String> permissionIds) {
		return null;
	}
	
	
}
