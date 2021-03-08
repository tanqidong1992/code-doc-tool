package com.hngd.codegen.constant;

import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

public class TestConstants {

    public static final String JAVA_SRC_ROOT="./src/test/java";
    
    
    /**
     * 系统角色管理
     * 
     * @author tqd
     */
    @RestController
    @RequestMapping("/role")
    @Validated
    public class RoleController {
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
        public List<String> getPagedRoles(
                @Min(1)@RequestParam("pageNo")Integer pageNo,
                @Min(1)@RequestParam("pageSize")Integer pageSize,
                @RequestParam(value="roleName",required=false)String roleName) {
            
            return null;
        }
    }
}
