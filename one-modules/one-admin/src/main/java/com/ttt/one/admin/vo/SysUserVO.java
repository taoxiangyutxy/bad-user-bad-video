package com.ttt.one.admin.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ttt.one.admin.entity.SysRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Schema(description = "后台用户")
@Data
public class SysUserVO {
    @Schema(description = "用户ID")
    private Long userId;
    @NotEmpty
    @Schema(description = "用户名")
    private String username;
    @NotEmpty
    @Schema(description = "密码")
    private String password;
    @Schema(description = "盐")
    private String salt;
    @Schema(description = "邮箱")
    private String email;
    @NotEmpty
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
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
    
    // 记住我功能字段
    @Schema(description = "记住我")
    private Boolean rememberMe = false;
    @Schema(description = "确认密码")
    private String confirmPassword;
}