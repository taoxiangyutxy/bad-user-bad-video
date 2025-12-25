package com.ttt.one.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Schema(description = "后台用户表")
@Data
@TableName("sys_user")
public class SysUser {
    @Schema(description = "用户ID")
    @TableId
    private Long userId;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "盐")
    private String salt;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "手机号")
    private String mobile;
    @Schema(description = "用户状态 0-禁用，1-启用")
    private Integer status;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "创建者ID")
    private Long createUserId;
    @Schema(description = "更新时间")
    private Date updateTime;
    @Schema(description = "更新者ID")
    private Long updateUserId;
    @Schema(description = "性别")
    private String gender;
    // 非数据库字段，用户拥有的角色
    @TableField(exist = false)
    private List<SysRole> roles;
}
