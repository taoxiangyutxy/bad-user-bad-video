package com.ttt.one.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 角色表
 * 
 * @author ttt
 * @email 496427196@qq.com
 * @date 2022-05-02 19:24:18
 */
@Data
@TableName("sys_role")
public class SysRoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 2-内部 3-外部
	 */
	private String type;
	/**
	 * 角色码
	 */
	private String code;
	/**
	 * 角色名
	 */
	private String name;
	/**
	 * 角色等级
	 */
	private Integer level;
	/**
	 * 状态  1-启用 2-禁用
	 */
	private String status;
	/**
	 * 10-仅个人 21-仅归属地 22-仅Project 30-归属地与Project 40-全系统
	 */
	private Integer dataScope;
	/**
	 * 字典表项类别 54-业务 55-财务 56-管理 57-其他
	 */
	private Integer typeId;
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
