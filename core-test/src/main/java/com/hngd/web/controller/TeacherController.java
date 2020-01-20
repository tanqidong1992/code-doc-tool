package com.hngd.web.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.common.web.result.RestResponse;
import com.hngd.test.dto.Teacher;
/**
 * 老师信息管理
 * @author tqd
 *
 */
@RequestMapping("/teacher")
@RestController
public class TeacherController {

	/**
	 * 新增老师信息
	 * @param teacher 待新增的老师信息
	 * @return
	 */
	@PostMapping("/add")
	public RestResponse<String> addTeacher(@RequestBody Teacher teacher){
		
		return null;
	}
	
	/**
	 * 删除老师信息
	 * @summary summary for deleteTeacher
	 * @description description for deleteTeacher
	 * @param id 待删除老师的ID
	 * @return
	 */
	@DeleteMapping("/")
	public RestResponse<String> deleteTeacher(@RequestParam("id") String id){
		
		return null;
	}
}
