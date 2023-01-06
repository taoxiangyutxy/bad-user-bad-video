package com.ttt.one.waiguagg.config;

import com.ttt.one.waiguagg.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web  配置
 */
@Configuration
public class WaiguaWebConfiguration implements WebMvcConfigurer {
    @Autowired
    private LoginUserInterceptor loginUserInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //当前服务下的 所有请求 都要被 loginUserInterceptor拦截器拦截
        registry.addInterceptor(loginUserInterceptor)
             //   .addPathPatterns("/**")
                //所有请求 不拦截
        .excludePathPatterns("/**");


    }
}
