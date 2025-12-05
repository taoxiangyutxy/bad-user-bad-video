package com.ttt.one.waiguagg.feign.fallback;

import com.ttt.one.common.utils.R;
import com.ttt.one.waiguagg.feign.ThirdPartyFeignServer;
import org.springframework.stereotype.Component;

@Component
public class ThirdPartyFeignServerFallback implements ThirdPartyFeignServer {
    @Override
    public R sendCode(String phone, String code) {
        return R.error(500, "短信服务失效");
    }
}
