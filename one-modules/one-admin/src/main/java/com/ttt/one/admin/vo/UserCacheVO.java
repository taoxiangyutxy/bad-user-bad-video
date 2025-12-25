package com.ttt.one.admin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserCacheVO implements Serializable {
    private Long userId;
    private String username;
    private List<String> authorities;  // 权限字符串列表
}
