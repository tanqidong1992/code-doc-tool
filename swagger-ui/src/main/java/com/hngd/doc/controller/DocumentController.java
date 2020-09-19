package com.hngd.doc.controller;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;


import com.hngd.common.exception.HNException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hngd.common.error.ErrorCode;
import com.hngd.common.result.Result;
import com.hngd.common.web.RestResponses;
import com.hngd.common.web.result.RestResponse;
import com.hngd.doc.SwaggerFileLoader;
import com.hngd.doc.entity.DocumentInfo;
import com.hngd.doc.swagger.TagFilter;
import com.hngd.doc.utils.FileDigest;

import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * 接口文档管理
 * @author tqd
 *
 */
@RestController
@RequestMapping("/api/document")
public class DocumentController {

	private static final Logger logger=LoggerFactory.getLogger(DocumentController.class);
	/**
	 * 上传接口文档
	 * @param file 接口文件
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
	@PostMapping("/upload")
	public RestResponse<Void> uploadDocument(@RequestParam("file")MultipartFile file) throws NoSuchAlgorithmException, IOException{
		
		String sha256=FileDigest.sha256Hex(file);
		String filename=file.getOriginalFilename();
		String ext=FilenameUtils.getExtension(filename);
		if(!"json".equals(ext)) {
			return RestResponses.newFailResponse(ErrorCode.INVALID_PARAMETER, "必须是Json文件");
		}
		Result<Void> r=SwaggerFileLoader.isValidFile(file);
		if(!r.isSuccess()) {
			return RestResponses.newResponseFromResult(r);
		}
		String targetFilename=sha256+"."+ext;
		File dest=new File(SwaggerFileLoader.root,targetFilename);
		if(dest.exists()) {
			 return RestResponses.newSuccessResponse();
		}
		logger.info("start transfer file{} to {}",file.getOriginalFilename(),dest.getAbsolutePath());
		try {
			//dest必须使用绝对路径
			file.transferTo(dest);
		} catch (IllegalStateException | IOException e) {
			logger.error("",e);
			return RestResponses
			    .newFailResponse(ErrorCode.SERVER_INTERNAL_ERROR, e.getMessage());
		}
		SwaggerFileLoader.addOriginFile(dest);
		return RestResponses.newSuccessResponse();
	}
	/**
	 * 加载所有的接口文档
	 * @return
	 */
	@GetMapping("/list/all")
	public RestResponse<List<DocumentInfo>> getAllDocument(){
		List<DocumentInfo> di=SwaggerFileLoader.loadAll();
	    return RestResponses.newSuccessResponse("",  di);
	}


	/**
	 * 加载所有接口文档的原文件
	 * @return
	 */
	@GetMapping("/origin/file/list")
	public RestResponse<List<DocumentInfo>> getAllOriginDocument(){
		List<DocumentInfo> di=SwaggerFileLoader.loadAll();
		return RestResponses.newSuccessResponse("", di);
	}

    /**
     * 删除接口文档源文件
     * @param filename 文件名
     * @return
     */
	@PostMapping("/origin/file/delete")
	public RestResponse<Void> deleteOriginFile(@RequestParam("filename")String filename){
		File file=new File(SwaggerFileLoader.root,filename);
		if(!file.exists()) {
			return RestResponses.newFailResponse(ErrorCode.TARGET_NOT_FOUND,"origin file not exists");
		}else {
			try{
				FileUtils.moveFileToDirectory(file,SwaggerFileLoader.history,true);
			}catch (IOException e){
				throw new HNException(ErrorCode.SERVER_INTERNAL_ERROR,"删除文件失败",e);
			}
			SwaggerFileLoader.deleteOriginFile(file);
			return RestResponses.newSuccessResponse();
		}
	}

	/**
	 * 加载接口文档
	 * @param filename 待加载接口文档源文件名称 
	 * @return
	 */
	@GetMapping("/info/{filename}")
	public String loadDocument(@PathVariable("filename")String filename){
	    File file=new File(SwaggerFileLoader.root,filename);
		if(!file.exists()) {
			 return "";
		}else {
		    String s=null;
			try {
			    s = FileUtils.readFileToString(file, "utf-8");
			} catch (IOException e) {
				logger.error("",e);
			}
			return s;
        }
	}
	
	/**
	 * 按照Tag过滤并加载指定接口文档
	 * @param filename 接口文档源文件名称
	 * @param tag 过滤模块名称,可选过滤参数
	 * @return
	 */
	@GetMapping("/info/{filename}/{tag}")
	public String loadDocumentByTag(@PathVariable(
			"filename")String filename,
			@PathVariable("tag")String tag){
        File file=new File(SwaggerFileLoader.root,filename);
		if(!file.exists()) {
			return "";
		}else {
			OpenAPI openAPI=SwaggerFileLoader.loadFromFile(file);
			TagFilter tagFilter=new TagFilter(tag);
			SpecFilter filter=new SpecFilter();
			openAPI=filter.filter(openAPI, tagFilter, null, null, null);
			try {
			    return SwaggerFileLoader.toJson(openAPI);
			} catch (JsonProcessingException e) {
				logger.error("",e);
		    }
        }
		return "";
	}
	 
}
