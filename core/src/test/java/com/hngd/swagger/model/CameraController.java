package com.hngd.swagger.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.common.web.result.RestResponse;
 

/**
 * 摄像机管理
 * 
 * @author tqd
 * @param <CameraInfo>
 */
@Controller
@RequestMapping("/camera")
public class CameraController<CameraInfo> {

     
}
