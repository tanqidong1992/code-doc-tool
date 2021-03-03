package com.hngd.doc.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import com.hngd.common.exception.HNException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.assertj.core.util.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.common.error.ErrorCode;
import com.hngd.common.result.Result;
import com.hngd.common.web.RestResponses;
import com.hngd.common.web.result.RestResponse;
import com.hngd.doc.constants.Constants;
import com.hngd.doc.core.OpenAPIFileManager;
import com.hngd.doc.entity.DocumentInfo;
import com.hngd.doc.swagger.TagFilter;
import com.hngd.doc.utils.DocumentUtils;
import com.hngd.doc.utils.FileDigest;
import com.hngd.doc.utils.OpenAPIFileUtils;
import com.hngd.s2m.OpenAPIToMarkdown;

import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * 接口文档管理
 * 
 * @author tqd
 *
 */
@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    
    @Autowired
    private OpenAPIFileManager openAPIManager;
    /**
     * 上传接口文档
     * 
     * @param file 接口文件
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @PostMapping("/upload")
    public RestResponse<Void> uploadDocument(@RequestParam("file") MultipartFile file)
            throws NoSuchAlgorithmException, IOException {

        String sha256 = FileDigest.sha256Hex(file);
        String filename = file.getOriginalFilename();
        String ext = FilenameUtils.getExtension(filename);
        if (!"json".equals(ext)) {
            return RestResponses.newFailResponse(ErrorCode.INVALID_PARAMETER, "必须是Json文件");
        }
        Result<Void> r = DocumentUtils.isValidFile(file);
        if (!r.isSuccess()) {
            return RestResponses.newResponseFromResult(r);
        }
        String targetFilename = sha256 + "." + ext;
        File dest = new File(openAPIManager.getData(), targetFilename);
        if (dest.exists()) {
            return RestResponses.newSuccessResponse();
        }
        logger.info("start transfer file{} to {}", file.getOriginalFilename(), dest.getAbsolutePath());
        try {
            // dest必须使用绝对路径
            file.transferTo(dest);
        } catch (IllegalStateException | IOException e) {
            logger.error("", e);
            return RestResponses.newFailResponse(ErrorCode.SERVER_INTERNAL_ERROR, e.getMessage());
        }
        openAPIManager.addOpenAPIFile(dest);
        return RestResponses.newSuccessResponse();
    }

    /**
     * 加载所有的接口文档
     * 
     * @return
     */
    @GetMapping("/list/all")
    public RestResponse<List<DocumentInfo>> getAllDocument() {
        List<DocumentInfo> di = openAPIManager.getAllDocuments();
        return RestResponses.newSuccessResponse("", di);
    }

    /**
     * 加载所有接口文档的原文件
     * 
     * @return
     */
    @GetMapping("/origin/file/list")
    public RestResponse<List<DocumentInfo>> getAllOriginDocument() {
        List<DocumentInfo> di = openAPIManager.getAllDocuments();
        return RestResponses.newSuccessResponse("", di);
    }

    /**
     * 删除接口文档源文件
     * 
     * @param filename 文件名
     * @return
     */
    @PostMapping("/origin/file/delete")
    public RestResponse<Void> deleteOriginFile(@RequestParam("filename") String filename) {
        File file = new File(openAPIManager.getData(), filename);
        if (!file.exists()) {
            return RestResponses.newFailResponse(ErrorCode.TARGET_NOT_FOUND, "origin file not exists");
        } else {
            try {
                FileUtils.moveFileToDirectory(file, openAPIManager.getHistory(), true);
            } catch (IOException e) {
                throw new HNException(ErrorCode.SERVER_INTERNAL_ERROR, "删除文件失败", e);
            }
            openAPIManager.deleteOpenAPIFile(file);
            return RestResponses.newSuccessResponse();
        }
    }

    /**
     * 加载接口文档
     * 
     * @param filename 待加载接口文档源文件名称
     * @return
     */
    @GetMapping("/info/{filename}")
    public String loadDocument(@PathVariable("filename") String filename) {
        File file = new File(openAPIManager.getData(), filename);
        if (!file.exists()) {
            return "";
        } else {
            String s = null;
            try {
                s = FileUtils.readFileToString(file, Constants.DEFAULT_CHARSET_NAME);
            } catch (IOException e) {
                logger.error("", e);
            }
            return s;
        }
    }

    /**
     * 导出接口文档为markdown
     * 
     * @param filename 待导出接口文档源文件名称
     * @return
     * @throws IOException
     */
    @GetMapping("/info/markdown/{filename}")
    public ResponseEntity<byte[]> exportDocumentAsMarkdown(@PathVariable("filename") String filename)
            throws IOException {
        File file = new File(openAPIManager.getData(), filename);
        File outputDirectory = Files.newTemporaryFolder();
        File markdownFile = OpenAPIToMarkdown.openAPIToMarkdown(file, null, outputDirectory);
        byte[] data = FileUtils.readFileToByteArray(markdownFile);
        String fileName = markdownFile.getName();
        HttpHeaders headers = new HttpHeaders();
        try {
            fileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
        }
        headers.setContentType(MediaType.TEXT_MARKDOWN);
        headers.set("Content-Disposition", "attachment;fileName=" + fileName);
        HttpStatus statusCode = HttpStatus.OK;
        return new ResponseEntity<byte[]>(data, headers, statusCode);
    }

    /**
     * 按照Tag过滤并加载指定接口文档
     * 
     * @param filename 接口文档源文件名称
     * @param tag      过滤模块名称,可选过滤参数
     * @return
     */
    @GetMapping("/info/{filename}/{tag}")
    public String loadDocumentByTag(@PathVariable("filename") String filename, @PathVariable("tag") String tag) {
        File file = new File(openAPIManager.getData(), filename);
        if (!file.exists()) {
            return "";
        } else {
            Optional<OpenAPI> openAPI = OpenAPIFileUtils.loadFromFile(file);
            if(openAPI.isEmpty()) {
                return "";
            }
            TagFilter tagFilter = new TagFilter(tag);
            SpecFilter filter = new SpecFilter();
            OpenAPI filteredOpenAPI = filter.filter(openAPI.get(), tagFilter, null, null, null);
            return Json.pretty(filteredOpenAPI);
        }
    }

    /**
     * 按照Tag过滤并导出指定接口文档
     * 
     * @param filename 接口文档源文件名称
     * @param tag      过滤模块名称,可选过滤参数
     * @return
     * @throws IOException
     */
    @GetMapping("/info/markdown/{filename}/{tag}")
    public ResponseEntity<byte[]> exportDocumentAsMarkdownByTag(@PathVariable("filename") String filename,
            @PathVariable("tag") String tag) throws IOException {
        File file = new File(openAPIManager.getData(), filename);
        Optional<OpenAPI> openAPI = OpenAPIFileUtils.loadFromFile(file);
        TagFilter tagFilter = new TagFilter(tag);
        SpecFilter filter = new SpecFilter();
        OpenAPI filteredOpenAPI = filter.filter(openAPI.get(), tagFilter, null, null, null);
        File outputDirectory = Files.newTemporaryFolder();
        File markdownFile = OpenAPIToMarkdown
                .openAPIToMarkdown(filteredOpenAPI, null, outputDirectory);
        byte[] data = FileUtils.readFileToByteArray(markdownFile);
        // String fileName=markdownFile.getName();
        String fileName = filteredOpenAPI.getInfo().getTitle() +
                "-" + tag + "-" + filteredOpenAPI.getInfo().getVersion() +
                ".md";
        HttpHeaders headers = new HttpHeaders();
        try {
            fileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
        }
        headers.setContentType(MediaType.TEXT_MARKDOWN);
        headers.set("Content-Disposition", "attachment;fileName=" + fileName);
        HttpStatus statusCode = HttpStatus.OK;
        return new ResponseEntity<byte[]>(data, headers, statusCode);
    }

}
