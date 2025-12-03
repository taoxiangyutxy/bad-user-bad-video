package com.ttt.one.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 会员实体类
 */
@Data
@TableName("waigua_user")
@Schema(description = "会员实体")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 会员ID
	 */
	@TableId
	@Schema(description = "会员ID")
	private Long id;
	/**
	 * 会员等级ID
	 */
	@Schema(description = "会员等级ID")
	private Long levelId;
	/**
	 * 用户名
	 */
	@Schema(description = "用户名")
	private String username;
	/**
	 * 密码
	 */
	@Schema(description = "密码")
	private String password;
	/**
	 * 昵称
	 */
	@Schema(description = "昵称")
	private String nickname;
	/**
	 * 手机号码
	 */
	@Schema(description = "手机号码")
	private String mobile;
	/**
	 * 邮箱地址
	 */
	@Schema(description = "邮箱地址")
	private String email;
	/**
	 * 头像URL
	 */
	@Schema(description = "头像URL")
	private String header;
	/**
	 * 性别 0-未知 1-男 2-女
	 */
	@Schema(description = "性别 0-未知 1-男 2-女")
	private Integer gender;
	/**
	 * 生日
	 */
	@Schema(description = "生日")
	private Date birth;
	/**
	 * 所在城市
	 */
	@Schema(description = "所在城市")
	private String city;
	/**
	 * 职业
	 */
	@Schema(description = "职业")
	private String job;
	/**
	 * 个性签名
	 */
	@Schema(description = "个性签名")
	private String sign;
	/**
	 * 用户来源 1-PC 2-Android 3-iOS 4-微信小程序
	 */
	@Schema(description = "用户来源 1-PC 2-Android 3-iOS 4-微信小程序")
	private Integer sourceType;
	/**
	 * 积分
	 */
	@Schema(description = "积分")
	private Integer integration;
	/**
	 * 成长值
	 */
	@Schema(description = "成长值")
	private Integer growth;
	/**
	 * 启用状态 0-禁用 1-启用
	 */
	@Schema(description = "启用状态 0-禁用 1-启用")
	private Integer status;
	/**
	 * 注册时间
	 */
	@Schema(description = "注册时间")
	private Date createTime;

}
