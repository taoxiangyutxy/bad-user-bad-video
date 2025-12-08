package com.ttt.one.waiguagg.feign.fallback;

import com.ttt.one.common.utils.R;
import com.ttt.one.common.vo.UserEntity;
import com.ttt.one.waiguagg.feign.UserFeignServer;
import org.springframework.stereotype.Component;

@Component
public class UserFeignServerFallback implements UserFeignServer {
    @Override
    public R info(Long id) {
        return R.error(500,"用户服务异常");
    }

    @Override
    public R update(UserEntity user) {
        return  R.error(500,"用户服务异常");
    }
}
