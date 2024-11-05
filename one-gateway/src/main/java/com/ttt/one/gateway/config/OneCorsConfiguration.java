package com.ttt.one.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 解决跨域配置
 */
@Configuration
public class OneCorsConfiguration {
    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //配置跨域
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
      //  corsConfiguration.addAllowedOrigin("*"); SpringBoot2.0.0中可用
      //  corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:8081","*"));SpringBoot2.0.0中可用
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList("http://localhost:8081","*"));//SpringBoot2.4.0之后可用
        corsConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);

    }
}
