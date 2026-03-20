package com.ttt.one.auth.fegin.fallback;

import com.ttt.one.auth.fegin.UserFeignServer;
import com.ttt.one.auth.vo.UserLoginVo;
import com.ttt.one.auth.vo.UserRegistVo;
import com.ttt.one.common.utils.R;
import org.springframework.stereotype.Component;

/**
 * feign   兜底回调
 */
@Component
public class UserFeignServerFallback implements UserFeignServer {
    @Override
    public R regist(UserRegistVo vo) {
        return R.error(500, "用户服务异常");
    }

    @Override
    public R login(UserLoginVo vo) {
        return R.error(500, "用户服务异常");

    }

    @Override
    public R getUserByUsername(String username) {
        return R.error(500, "用户服务异常");
    }

    @Override
    public R resetPassword(String username, String newPassword) {
        return R.error(500, "用户服务异常");
    }
}
