package com.ttt.one.waiguagg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession//整合redis作为session存储
@MapperScan("com.ttt.one.waiguagg.dao")
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class OneWaiguaggApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneWaiguaggApplication.class, args);
    }

}
