package com.ttt.one.auth.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * feign 拦截器
 */
@Component
public class TokenRequestInterceptor implements RequestInterceptor {
    /**
     * 请求拦截器
     * @param requestTemplate   请求模版
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        System.out.println("feign 拦截器启动==");
        requestTemplate.header("X-Token", UUID.randomUUID().toString());
    }
}
