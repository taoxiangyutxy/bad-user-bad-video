package com.ttt.one.auth.fegin;

import com.ttt.one.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("one-third-party")
public interface ThirdPartyFeginServer {
    /**
     * 发送短信
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sms/sendSms")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
