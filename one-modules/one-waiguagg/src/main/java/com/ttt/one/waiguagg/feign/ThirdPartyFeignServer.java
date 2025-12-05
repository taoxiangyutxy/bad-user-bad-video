package com.ttt.one.waiguagg.feign;

import com.ttt.one.common.utils.R;
import com.ttt.one.waiguagg.feign.fallback.ThirdPartyFeignServerFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "one-third-party",fallback = ThirdPartyFeignServerFallback.class)
public interface ThirdPartyFeignServer {
    /**
     * 发送短信
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sms/sendSms")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
