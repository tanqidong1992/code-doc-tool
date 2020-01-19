package com.hngd.test.dto;

import java.util.LinkedList;
import java.util.List;

import com.hngd.model.Menu;

import lombok.Data;

/**
 * 菜单树节点信息
 * @author hnoe-tqd
 *
 */
@Data
public class MenuTreeNode extends Menu{

	/**
	 * 子菜单项
	 */
	private List<MenuTreeNode> subMenus=new LinkedList<>();
	/**
	 * 对应页面路径
	 */
	private String pageUrl;
	
}
