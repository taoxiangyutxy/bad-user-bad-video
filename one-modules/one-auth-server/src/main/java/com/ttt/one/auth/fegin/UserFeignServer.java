package com.ttt.one.auth.fegin;

import com.ttt.one.auth.fegin.fallback.UserFeignServerFallback;
import com.ttt.one.auth.vo.UserLoginVo;
import com.ttt.one.auth.vo.UserRegistVo;
import com.ttt.one.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户模块
 */
@FeignClient(value = "one-user" ,fallback = UserFeignServerFallback.class)
public interface UserFeignServer {
    /**
     * 用户注册
     * @param vo
     * @return
     */
    @PostMapping("/user/user/regist")
     R regist(@RequestBody UserRegistVo vo);

    /**
     * 用户登录
     * @param vo
     * @return
     */
    @PostMapping("/user/user/login")
     R login(@RequestBody UserLoginVo vo);

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @PostMapping("/user/user/getByUsername")
     R getUserByUsername(@RequestParam("username")  String username);

    /**
     * 重置用户密码
     * @param username 用户名
     * @param newPassword 新密码
     * @return 操作结果
     */
    @PostMapping("/user/user/resetPassword")
     R resetPassword(@RequestParam("username") String username, @RequestParam("newPassword") String newPassword);
}
