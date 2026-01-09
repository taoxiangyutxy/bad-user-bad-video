package com.ttt.one.waiguagg.feign.fallback;

import com.ttt.one.common.utils.R;
import com.ttt.one.common.vo.UserEntity;
import com.ttt.one.waiguagg.feign.AdminUserFeignServer;
import com.ttt.one.waiguagg.feign.UserFeignServer;
import org.springframework.stereotype.Component;

@Component
public class AdminUserFeignServerFallback implements AdminUserFeignServer {

    @Override
    public R getCurrentUser() {
        return R.error(500,"后台管理平台服务异常");
    }
}
