package com.ttt.one.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(description = "后台角色表")
@Data
public class SysRole {
    @Schema(description = "角色ID")
    @TableId
    private Long roleId;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "角色描述")
    private String description;
    @Schema(description = "创建者ID")
    private Long createUserId;;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "角色编码")
    private String roleCode;
}
