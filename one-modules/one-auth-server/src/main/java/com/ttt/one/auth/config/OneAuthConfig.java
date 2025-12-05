package com.ttt.one.auth.config;

import feign.Logger;
import feign.Retryer;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * OneAuthConfig 配置类
 *
 */
@Configuration
public class OneAuthConfig {
    /**
     * feign 日志级别
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    /**
     * feign 重试
     * @return
     */
    @Bean
    Retryer retryer() {
        return new Retryer.Default();
    }
}
