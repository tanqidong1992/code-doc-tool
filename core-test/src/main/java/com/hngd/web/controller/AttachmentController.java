package com.hngd.web.controller;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.common.error.ErrorCode;
import com.hngd.common.result.Result;
import com.hngd.common.web.RestResponses;
import com.hngd.common.web.auth.User;
import com.hngd.common.web.context.HttpRequestContext;
import com.hngd.common.web.page.PagedData;
import com.hngd.common.web.result.RestResponse;
/**
 * 文件上传接口测试
 * @author tqd
 *
 */
@RestController
@RequestMapping("/attachment")
@Validated
public class AttachmentController {

      /**
     * 上传附件
     * @param file 附件
     * @param ownerId 业务对象Id
     * @param ownerTypeCode 业务对象类型代码,默认值为-1
     * @param typeCode 附件类型代码,默认值为-1
     * @param tags 附件标签,多个以','分隔
     * @return
     */
    @PostMapping("/upload")
    public RestResponse<String> uploadAttachment(
            @RequestPart("file")MultipartFile file,
            @RequestPart("ownerId")String ownerId,
            @RequestPart(value="ownerTypeCode",required=false)String ownerTypeCode,
            @RequestPart(value="typeCode",required=false)String typeCode,
            @Size(max=1024)@RequestPart(value="tags",required=false)String tags){
        
         
        return RestResponses.newSuccessResponse("","");
    }
    /**
     * 批量上传附件
     * @param files 附件集合
     * @param ownerId 业务对象Id
     * @param ownerTypeCode 业务对象类型代码,默认值为-1
     * @param typeCode 附件类型代码,默认值为-1
     * @param tags 附件标签,多个以','分隔
     * @return
     */
    @PostMapping("/batch/upload")
    public RestResponse<String> uploadBatchAttachment(
            @RequestPart("files")MultipartFile[] files,
            @RequestPart("ownerId")String ownerId,
            @RequestPart(value="ownerTypeCode",required=false)String ownerTypeCode,
            @RequestPart(value="typeCode",required=false)String typeCode,
            @Size(max=1024)@RequestPart(value="tags",required=false)String tags){
        return RestResponses.newSuccessResponse("","");
    }
}
