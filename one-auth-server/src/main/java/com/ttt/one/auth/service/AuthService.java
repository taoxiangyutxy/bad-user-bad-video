package com.ttt.one.auth.service;

import com.ttt.one.common.utils.R;

public interface AuthService {
    /**
     * 获取验证码
     * @param phone
     * @return
     */
    R sendCode(String phone);
}
