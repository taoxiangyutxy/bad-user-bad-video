package com.ttt.one.user.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 用户注册 vo
 */
@Data
@ToString
public class UserRegistVo {
    private String username;
    private String password;
    private String phone;

}
