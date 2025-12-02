package com.ttt.one.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 用户登录 vo
 */
@Data
public class UserLoginVo {
    @NotEmpty(message = "登录账号必须填写")
    @Schema(description = "登录账号")
    private String loginacct;
    @NotEmpty(message = "密码必须填写")
    @Schema(description = "密码")
    private String password;
}
