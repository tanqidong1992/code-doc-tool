package com.hngd.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class TagController {

	/**
	 * No Class Tag Test
	 * @author tqd
	 *
	 */
	@RestController
	@RequestMapping("no/class/tag")
	public static class NoClassTagController{
		/**
		 * get tag
		 * @param id the id of the tag to obtain
		 * @summary obtain tag by tag id
		 * @tags tag manager,tag,tqd
		 * @return
		 */
		@GetMapping("/")
		public String getTag(String id) {
			return id;
		}
	}
	
	
	/**
	 * Exists Class Tag Test
	 * @tags class tag1,class tag2
	 * @author tqd
	 *
	 */
	@RestController
	@RequestMapping("exits/class/tag")
	public static class ExistsClassTagController{
		/**
		 * get tag
		 * @param id the id of the tag to obtain
		 * @summary obtain tag by tag id
		 * @tags tag manager,tag,tqd
		 * @return
		 */
		@GetMapping("/")
		public String getTag(String id) {
			return id;
		}
	}
}
