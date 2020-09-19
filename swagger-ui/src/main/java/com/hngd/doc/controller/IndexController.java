package com.hngd.doc.controller;

import com.hngd.doc.SwaggerFileLoader;
import com.hngd.doc.entity.DocumentInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {

	@GetMapping("")
	public String index(Model model) {
		List<DocumentInfo> documents= SwaggerFileLoader.loadAll();
		model.addAttribute("documents",documents);
		return "index.html";
	}
	
	@GetMapping("/setting")
	public String setting(Model model) {
		List<DocumentInfo> documents= SwaggerFileLoader.loadAll();
		model.addAttribute("documents",documents);
		return "setting.html";
	}
}
