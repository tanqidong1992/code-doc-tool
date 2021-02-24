package com.hngd.doc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SwaggerEditorController {
    
    @GetMapping("/editor")
    public String editor(@RequestParam(value="docUrl",required=false)String docUrl) {
        return "/swagger-editor.html?docUrl="+docUrl;
    }
}
