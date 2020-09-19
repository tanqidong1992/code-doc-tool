package com.hngd.web.controller;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.common.error.ErrorCode;
import com.hngd.common.util.GsonUtils;
import com.hngd.common.web.RestResponses;
import com.hngd.common.web.parameter.GsonEditor;
import com.hngd.common.web.result.RestResponse;
import com.hngd.test.dto.ComplexDTO;
import com.hngd.test.dto.FormWithDate;
import com.hngd.test.dto.FormWithJacksonDate;
import com.hngd.test.dto.FormWithJson;
import com.hngd.test.dto.OtherInfo;
import com.hngd.test.dto.Person;


/**
 * 示例
 * 
 * @author tqd
 */
@RestController
@RequestMapping("/sample")
@Validated
public class SampleController {
 
	/**
	 * query参数中的日期测试
	 * @param date 日期
	 * @return
	 */
	@GetMapping("/echo/date")
	public RestResponse<Void> echoDate(
			//一般日期参数用yyyy-MM-dd格式
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("date") Date date) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String s=sdf.format(date);
		return RestResponses.newSuccessResponse(s);
	}
    /**
     * 时间戳测试
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
	@GetMapping("/echo/time")
	public RestResponse<Void> echoTime(
			//一般时间参数,直接用时间戳
			 @Min(0)@RequestParam("startTime") Long startTime,
			 @Min(0)@RequestParam("endTime") Long endTime) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String s=sdf.format(new Date(startTime))+"-"+sdf.format(new Date(endTime));
		return RestResponses.newSuccessResponse(s);
	}
 
	
    
	
	/**
	 * 表单里面日期测试
	 * @param date 日期
	 * @return
	 */
	@PostMapping("/form/with/date")
	public RestResponse<FormWithDate> formWithDate(FormWithDate date) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String s=sdf.format(date.getDate());
		return RestResponses.newSuccessResponse(s,date);
	}
	
	/**
	 *  body里面的日期测试
	 * @param date 日期
	 * @return
	 */
	@PostMapping("/body/with/date")
	public RestResponse<FormWithDate> bodyWithDate(@RequestBody FormWithDate date) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String s=sdf.format(date.getDate());
		return RestResponses.newSuccessResponse(s,date);
	}
	
	/**
	 *  body里面的jackson日期测试
	 * @param date 日期
	 * @return
	 */
	@PostMapping("/body/with/jackson/date")
	public RestResponse<FormWithJacksonDate> bodyWithJacksonDate(@RequestBody FormWithJacksonDate date) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String s=sdf.format(date.getDate());
		return RestResponses.newSuccessResponse(s,date);
	}
	
	/**
	 * body json 测试
	 * @param json json
	 * @return
	 */
	@PostMapping("/body/with/json")
	public RestResponse<FormWithJson> bodyWithJson(@RequestBody FormWithJson json) {
		return RestResponses.newSuccessResponse(json);
	}
	@InitBinder
	public void onDataBinderInit(WebDataBinder binder) {
		//formWithJson
		//used to convert json str to OtherInfo object
		binder.registerCustomEditor(OtherInfo.class, new GsonEditor(OtherInfo.class));
	}
	/**
	 * form json 测试
	 * @param json json
	 * @return
	 */
	@PostMapping("/form/with/json")
	public RestResponse<FormWithJson> formWithJson(FormWithJson json) {
		return RestResponses.newSuccessResponse(json);
	}
	/**
	 * body simple string 测试
	 * @param json 简单json
	 * @return
	 */
	@PostMapping("/body/string")
	public RestResponse<String> bodyWithString(@RequestBody String json) {
		return RestResponses.newSuccessResponse(json,json);
	}
	/**
	 * ModelAttribute注解测试
	 * @param oi 测试实体
	 * @return
	 */
	@GetMapping("/model/attribute/test")
	public RestResponse<OtherInfo> modelTest(@ModelAttribute OtherInfo oi){
		return RestResponses.newSuccessResponse(oi);
	}
    /**
     * Test1
     * @param oi
     * @return
     */
	@GetMapping("/model/attribute/test1")
	public RestResponse<OtherInfo> modelTest1(@ModelAttribute OtherInfo oi){
		return RestResponses.newSuccessResponse(oi);
	}
	/**
	 * list test
	 * @param dates list items
	 * @return
	 */
	@PostMapping("/body/with/date/list")
	public RestResponse<Void> bodyWithListDate(@RequestBody List<FormWithDate> dates) {
		 
		return RestResponses.newSuccessResponse("xxx");
	}
	
	/**
	 * str list test
	 * @param strs list items
	 * @return
	 */
	@PostMapping("/body/with/string/list")
	public RestResponse<Void> bodyWithListString(@RequestBody List<String> strs) {
		 
		return RestResponses.newSuccessResponse("xxx");
	}
	
	/**
	 * list test
	 * @param dates list items
	 * @return
	 */
	@GetMapping("/form/with/date/list")
	public RestResponse<Void> queryWithListDate(@RequestParam("dates")List<FormWithDate> dates) {
		 
		return RestResponses.newSuccessResponse("xxx");
	}
	
	/**
	 * test map parameter
	 * @param params map parameter
	 * @return
	 */
	@GetMapping("/map")
	public RestResponse<Map<String,Object>> addPreEvlAndUpdateRisk(@RequestParam(required = false) Map<String, Object> params){
		/**
          Annotation which indicates that a method parameter should be bound to a webrequest parameter. 

          Supported for annotated handler methods in Servlet and Portlet environments. 

          If the method parameter type is Map and a request parameter nameis specified, then the request parameter value is converted to a Mapassuming an appropriate conversion strategy is available. 

          If the method parameter is Map<String, String> or MultiValueMap<String, String>and a parameter name is not specified, then the map parameter is populatedwith all request parameter names and values.

		 */
		return RestResponses.newSuccessResponse("",params);
	}
	
	
	/**
	 * test map model
	 * @param params map model
	 * @return
	 */
	@GetMapping("/map/test")
	public RestResponse<Map<String,Object>> mapTest(Map<String, Object> params){
		//the same as Model
		return RestResponses.newSuccessResponse("",params);
	}
	/**
	 * 测试多种参数混合
	 * @param planId 计划Id
	 * @param dto 人员信息
	 * @param bindingResult 不知道哦
	 * @return
	 */
	 @PutMapping(value = "/scrap/approve/{id}")
	 public RestResponse<String> planApprove(@PathVariable("id") String planId, @RequestBody @Valid Person dto, BindingResult bindingResult){
		 return null;
	 }
	 /**
	  * 没有指定名称测试
	  * @param planId 计划id
	  * @param name 名称
	  * @param bindingResult
	  * @return
	  */
	 @GetMapping(value = "/scrap/approve1/test")
	 public RestResponse<String> planApprove1(@RequestParam String planId,@RequestParam String name,BindingResult bindingResult){
		 return null;
	 }
	 /**
	  * 测试返回复杂参数
	  * @return
	  */
	 @GetMapping("/test/complex")
	 public RestResponse<ComplexDTO> getComplexResp(){
		 return null;
	 }
}
