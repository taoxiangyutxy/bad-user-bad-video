package com.ttt.one.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源表
 * 
 * @author ttt
 * @email 496427196@qq.com
 * @date 2022-05-02 19:24:18
 */
@Data
@TableName("sys_resource")
public class SysResourceEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 父级id
	 */
	private Integer parentId;
	/**
	 * 组级
	 */
	private String ancestors;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * M目录 C菜单页 F按钮
	 */
	private String menuType;
	/**
	 * 资源名称 
	 */
	private String signDesc;
	/**
	 * 名称 唯一标识key
	 */
	private String signCode;
	/**
	 * 层级
	 */
	private Integer level;
	/**
	 * 
	 */
	private String url;
	/**
	 * 请求方法:  * GET POST PUT
	 */
	private String method;
	/**
	 * 菜单状态（0显示 1隐藏）
	 */
	private String visible;
	/**
	 * 所属微服务
	 */
	private String serverId;
	/**
	 * 显示顺序
	 */
	private Integer orderNum;
	/**
	 * 权限标识
	 */
	private String perms;
	/**
	 * 1-APP 2-PC
	 */
	private String dataType;
	/**
	 * 菜单图标
	 */
	private String icon;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 所属客户端 1 - APP  2 - PC  3-都有
	 */
	private Integer clientType;
	/**
	 * 排序
	 */
	private Integer sortNo;
	/**
	 * 创建人
	 */
	private Integer createdBy;
	/**
	 * 创建时间
	 */
	private Date createdTime;
	/**
	 * 更新人
	 */
	private Integer updatedBy;
	/**
	 * 创建时间
	 */
	private Date updatedTime;
	/**
	 * 0-正常 1-删除
	 */
	private String delFlag;

}
