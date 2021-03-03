package com.hngd.doc.controller;

import com.hngd.doc.config.DocumentProperties;
import com.hngd.doc.core.OpenAPIFileManager;
import com.hngd.doc.entity.DocumentInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    private OpenAPIFileManager openAPIManager;
    @Autowired
    private DocumentProperties docProperties;
    /**
     * 文档主页
     * @param model
     * @return
     */
    @GetMapping("")
    public String index(Model model) {
        List<DocumentInfo> documents= openAPIManager.getAllDocuments();
        injectCommentAttributes(model);
        model.addAttribute("documents",documents);
        return "index.html";
    }
    /**
     * 文档管理页面
     * @param model
     * @return
     */
    @GetMapping("/setting")
    public String setting(Model model) {
        List<DocumentInfo> documents= openAPIManager.getAllDocuments();
        injectCommentAttributes(model);
        model.addAttribute("documents",documents);
        return "setting.html";
    }
    private void injectCommentAttributes(Model model) {
        model.addAttribute("systemName", docProperties.getSystemName());
    }
}
