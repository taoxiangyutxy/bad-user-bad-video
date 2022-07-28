package com.ttt.one.waiguagg.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 远程调用  拦截器  解决远程调用 不带请求头
 */
@Configuration
public class WaiGuatttFeignConfig {
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //利用threadLocal 本地线程同步  拿到请求头
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                System.out.println("feign远程之前先执行这个");
                HttpServletRequest request = attributes.getRequest(); //获得老请求
                if(request!=null){
                    //同步请求头cookie数据至新请求
                    String cookie = request.getHeader("Cookie");
                    requestTemplate.header("Cookie",cookie);
                }
            }
        };
    }
}
