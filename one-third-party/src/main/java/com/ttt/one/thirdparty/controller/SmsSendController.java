package com.ttt.one.thirdparty.controller;

import com.ttt.one.common.utils.R;
import com.ttt.one.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsSendController {
    @Autowired
    private SmsComponent smsComponent;
    /**
     * 提供给别的服务 进行调用    而不是页面直接发该请求，因为我是第三方服务  是提供给多个服务方的接口方
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sendSms")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
        smsComponent.sendSmsCode(phone,code);
        return R.ok();
    }
}