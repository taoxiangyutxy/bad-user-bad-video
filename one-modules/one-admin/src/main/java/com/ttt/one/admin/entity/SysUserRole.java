package com.ttt.one.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Schema(description = "后台用户角色表")
@Data
@TableName("sys_user_role")
public class SysUserRole {
    @Schema(description = "ID")
    @TableId
    private Long id;
    @Schema(description = "用户ID")
    private Long userId;;
    @Schema(description = "角色ID")
    private Long roleId;

}
