package com.hngd.web.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

import com.hngd.common.web.result.RestResponse;

import java.time.LocalDate;
import java.util.List;
/**
 * 临时接口管理
 * @author tqd
 *
 */
@RequestMapping("/temp")
@RestController
public class TempController {

    /**
     * 批量删除部门
     * @param ids 待删除部门的Id集合
     * @return
     */
    @PostMapping("/delete")
    public RestResponse<String> deleteSelect(@RequestBody List<String> ids) {
        return null;
    }
     /**
      * 新增学生群体
      * @param ids 待新增学生群体
      * @return
      */
    @PostMapping("/nested/test")
    public RestResponse<String> addStudentGroup(@RequestBody StudentGroup ids) {
        return null;
    } 


    @Data
	public static class Person{
		/**
		 * 人员姓名
		 */
		private String name;
		/**
		 * 人员年龄
		 */
		private Integer age;
		/**
		 * 出生日期
		 */
		private LocalDate birthday;
	}
	@Data
	public static class School{
		/**
		 * 学校名称
		 */
		private String name;
		/**
		 * 学校地址
		 */
		private Integer address;
	}
	@Data
	public static class Student extends Person{
		/**
		 * 班级名称
		 */
		private String className;
		/**
		 * 学校信息
		 */
		private School school;
	}

	@Data
	public static class StudentGroup{

		/**
		 * 团体名称
		 */
		private String name;
		/**
		 * 学生列表
		 */
		private List<Student> students;
	}
}
