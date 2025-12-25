package com.ttt.one.admin.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserRoleVO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotEmpty(message = "角色ID列表不能为空")
    private String roleIds;
}
