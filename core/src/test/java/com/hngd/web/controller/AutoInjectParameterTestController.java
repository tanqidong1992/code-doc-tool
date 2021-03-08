package com.hngd.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.common.web.RestResponses;
import com.hngd.common.web.result.RestResponse;
/**
 * 自动参数测试
 * @author tqd
 *
 */
@RestController
@RequestMapping("/auto/inject")
public class AutoInjectParameterTestController {

    /**
     * HttpServletRequest Test
     * @param req  HttpServletRequest
     * @param name  the name of user
     * @return
     */
    @GetMapping("/req")
    public RestResponse<Void> getHttpRequest(HttpServletRequest req,@RequestParam("name")String name){
        return RestResponses.newSuccessResponse();
    }
    /**
     * Cookie Value Test
     * @param name cookie name
     * @param age the age of user
     * @return
     */
    @GetMapping("/cookie")
    public RestResponse<Void> getCookieValue(@CookieValue("name")String name
            ,@RequestParam("age")Integer age){
        return RestResponses.newSuccessResponse();
    }
    
    /**
     * Cookie Value Test
     * @param name cookie name
     * @return
     */
    @GetMapping("/cookie/only")
    public RestResponse<Void> getCookieValueOnly(@CookieValue("name")String name){
        return RestResponses.newSuccessResponse();
    }
}
