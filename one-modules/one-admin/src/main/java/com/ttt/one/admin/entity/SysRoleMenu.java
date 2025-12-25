package com.ttt.one.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "后台角色权限")
@Data
@TableName("sys_role_menu")
public class SysRoleMenu {
    @Schema(description = "ID")
    @TableId
    private Long id;
    @Schema(description = "角色ID")
    private Long roleId;
    @Schema(description = "权限ID")
    private Long menuId;;

}
