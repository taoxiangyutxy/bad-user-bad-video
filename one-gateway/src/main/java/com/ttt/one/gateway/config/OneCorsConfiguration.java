package com.ttt.one.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Collections;

/**
 * 跨域资源共享（CORS）配置
 * 用于网关层统一处理前端跨域请求
 */
@Configuration
public class OneCorsConfiguration {
    
    /**
     * 配置跨域过滤器
     * @return CorsWebFilter 跨域过滤器实例
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // 允许所有请求头
        corsConfig.addAllowedHeader("*");
        
        // 允许所有HTTP方法（GET、POST、PUT、DELETE等）
        corsConfig.addAllowedMethod("*");
        
        // 允许所有来源（生产环境建议配置具体域名）
        corsConfig.addAllowedOriginPattern("*");
        
        // 允许携带认证信息（如Cookie、Authorization头）
        corsConfig.setAllowCredentials(true);
        
        // 预检请求的有效期（单位：秒），减少OPTIONS请求次数
        corsConfig.setMaxAge(3600L);
        
        // 暴露给前端的响应头
        corsConfig.setExposedHeaders(Collections.singletonList("*"));
        
        // 注册CORS配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
