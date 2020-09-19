package com.hngd.parser.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 方法参数信息
 * @author tqd
 *
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ParameterInfo extends CommentDecoratedTarget{
	/**
	 * 参数类型名称
	 */
	private String typeName;
	 
}
