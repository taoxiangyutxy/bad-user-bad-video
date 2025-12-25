package com.ttt.one.admin.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class RoleMenuVO {
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
    @NotEmpty(message = "权限ID列表不能为空")
    private String menuIds;
}
