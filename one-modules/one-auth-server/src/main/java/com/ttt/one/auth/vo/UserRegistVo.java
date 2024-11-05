package com.ttt.one.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 用户注册 vo
 */
@Data
public class UserRegistVo {
  //  @NotEmpty(message = "用户名必须提交")
    @Length(min = 2,max = 20,message = "用户名必须是2-20位字符")
    private String username;
    @NotEmpty(message = "密码必须填写")
    private String password;
    //@NotEmpty(message = "手机号必须填写")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式不正确")
    private String phone;
    @NotEmpty(message = "验证码必须填写")
    private String code;
}
