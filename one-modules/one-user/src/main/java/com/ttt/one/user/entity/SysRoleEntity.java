package com.ttt.one.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 角色实体类
 */
@Data
@TableName("sys_role")
@Schema(description = "角色实体")
public class SysRoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 角色ID
	 */
	@TableId
	@Schema(description = "角色ID")
	private Integer id;
	/**
	 * 角色类型 2-内部 3-外部
	 */
	@Schema(description = "角色类型 2-内部 3-外部")
	private String type;
	/**
	 * 角色码
	 */
	@Schema(description = "角色码")
	private String code;
	/**
	 * 角色名
	 */
	@Schema(description = "角色名")
	private String name;
	/**
	 * 角色等级
	 */
	@Schema(description = "角色等级")
	private Integer level;
	/**
	 * 状态 1-启用 2-禁用
	 */
	@Schema(description = "状态 1-启用 2-禁用")
	private String status;
	/**
	 * 数据范围 10-仅个人 21-仅归属地 22-仅Project 30-归属地与Project 40-全系统
	 */
	@Schema(description = "数据范围 10-仅个人 21-仅归属地 22-仅Project 30-归属地与Project 40-全系统")
	private Integer dataScope;
	/**
	 * 字典表项类别 54-业务 55-财务 56-管理 57-其他
	 */
	@Schema(description = "字典表项类别 54-业务 55-财务 56-管理 57-其他")
	private Integer typeId;
	/**
	 * 创建人
	 */
	@Schema(description = "创建人")
	private Integer createdBy;
	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private Date createdTime;
	/**
	 * 更新人
	 */
	@Schema(description = "更新人")
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
