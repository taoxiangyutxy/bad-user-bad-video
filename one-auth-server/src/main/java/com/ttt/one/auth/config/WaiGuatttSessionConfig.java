package com.ttt.one.auth.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
/**
 *  描述: 分布式session共享 配置类
 * @param :
 * @return null
 * @author txy
 * @description
 * @date 2021/11/16 15:35
 */
@Configuration
public class WaiGuatttSessionConfig {
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        //设置cookie作用父域
        cookieSerializer.setDomainName("waiguattt.com");
        cookieSerializer.setCookieName("WAIGUATTTSESSION");
        return cookieSerializer;
    }

    /**
     * 修改session序列化机制
     * @return
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericFastJsonRedisSerializer();
    }
}

