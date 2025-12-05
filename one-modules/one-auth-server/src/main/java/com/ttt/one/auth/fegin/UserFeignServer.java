package com.ttt.one.auth.fegin;

import com.ttt.one.auth.fegin.fallback.UserFeignServerFallback;
import com.ttt.one.auth.vo.UserLoginVo;
import com.ttt.one.auth.vo.UserRegistVo;
import com.ttt.one.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
}
