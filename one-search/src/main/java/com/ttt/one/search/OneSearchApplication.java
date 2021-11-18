package com.ttt.one.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession//整合redis作为session存储
@EnableDiscoveryClient
@SpringBootApplication
public class OneSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneSearchApplication.class, args);
    }

}
