package com.ttt.one.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

/**
 * 用户注册 vo
 */
@Data
@ToString
@Schema(description = "用户注册 vo")
public class UserRegisterVo {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "手机号")
    private String phone;

}
