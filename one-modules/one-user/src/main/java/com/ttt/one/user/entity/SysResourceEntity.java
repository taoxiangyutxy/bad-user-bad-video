package com.ttt.one.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 系统资源实体类
 */
@Data
@TableName("sys_resource")
@Schema(description = "系统资源实体")
public class SysResourceEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 资源ID
	 */
	@TableId
	@Schema(description = "资源ID")
	private Integer id;
	/**
	 * 父级资源ID
	 */
	@Schema(description = "父级资源ID")
	private Integer parentId;
	/**
	 * 祖级列表
	 */
	@Schema(description = "祖级列表")
	private String ancestors;
	/**
	 * 资源标题
	 */
	@Schema(description = "资源标题")
	private String title;
	/**
	 * 菜单类型 M-目录 C-菜单 F-按钮
	 */
	@Schema(description = "菜单类型 M-目录 C-菜单 F-按钮")
	private String menuType;
	/**
	 * 资源描述
	 */
	@Schema(description = "资源描述")
	private String signDesc;
	/**
	 * 资源编码 唯一标识key
	 */
	@Schema(description = "资源编码 唯一标识key")
	private String signCode;
	/**
	 * 资源层级
	 */
	@Schema(description = "资源层级")
	private Integer level;
	/**
	 * 请求地址
	 */
	@Schema(description = "请求地址")
	private String url;
	/**
	 * 请求方法 GET/POST/PUT/DELETE
	 */
	@Schema(description = "请求方法 GET/POST/PUT/DELETE")
	private String method;
	/**
	 * 菜单状态 0-显示 1-隐藏
	 */
	@Schema(description = "菜单状态 0-显示 1-隐藏")
	private String visible;
	/**
	 * 所属微服务ID
	 */
	@Schema(description = "所属微服务ID")
	private String serverId;
	/**
	 * 显示顺序
	 */
	@Schema(description = "显示顺序")
	private Integer orderNum;
	/**
	 * 权限标识
	 */
	@Schema(description = "权限标识")
	private String perms;
	/**
	 * 数据类型 1-APP 2-PC
	 */
	@Schema(description = "数据类型 1-APP 2-PC")
	private String dataType;
	/**
	 * 菜单图标
	 */
	@Schema(description = "菜单图标")
	private String icon;
	/**
	 * 备注信息
	 */
	@Schema(description = "备注信息")
	private String remark;
	/**
	 * 客户端类型 1-APP 2-PC 3-都有
	 */
	@Schema(description = "客户端类型 1-APP 2-PC 3-都有")
	private Integer clientType;
	/**
	 * 排序号
	 */
	@Schema(description = "排序号")
	private Integer sortNo;
	/**
	 * 创建人ID
	 */
	@Schema(description = "创建人ID")
	private Integer createdBy;
	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private Date createdTime;
	/**
	 * 更新人ID
	 */
	@Schema(description = "更新人ID")
	private Integer updatedBy;
	/**
	 * 更新时间
	 */
	@Schema(description = "更新时间")
	private Date updatedTime;
	/**
	 * 删除标识 0-正常 1-删除
	 */
	@Schema(description = "删除标识 0-正常 1-删除")
	private String delFlag;

}
