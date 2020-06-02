package com.hngd.web.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.common.web.result.RestResponse;

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
}
