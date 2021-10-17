package com.ttt.one.auth.service.impl;

import com.ttt.one.auth.fegin.ThirdPartyFeginServer;
import com.ttt.one.auth.service.AuthService;
import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private ThirdPartyFeginServer thirdPartyFeginServer;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Override
    public R sendCode(String phone) {
        // TODO 1接口防刷

        //2验证码的再次校验
        String s = redisTemplate.opsForValue().get(Constant.SMS_CODE_CACHE_PREFIX + phone);
        if(!StringUtils.isEmpty(s)){
            long l = Long.parseLong(s.split("_")[1]);
            if(System.currentTimeMillis()-l < 60000){
                //60秒内不能再发
                log.info("请60后再发！");
                return R.error("请60秒后再发!");
            }
        }

        String code = UUID.randomUUID().toString().substring(0,5)+"_"+System.currentTimeMillis();
        //redis缓存验证码
        redisTemplate.opsForValue().set(Constant.SMS_CODE_CACHE_PREFIX+phone,code,10, TimeUnit.MINUTES);
        thirdPartyFeginServer.sendCode(phone,code.split("_")[0]);
        return R.ok();
    }
}
