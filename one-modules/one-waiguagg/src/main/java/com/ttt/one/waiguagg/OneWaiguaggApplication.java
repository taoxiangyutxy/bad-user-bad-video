package com.ttt.one.waiguagg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRabbit //开启MQ
@EnableRedisHttpSession//整合redis作为session存储
@MapperScan("com.ttt.one.waiguagg.dao")
//使公共全局异常处理器在该服务生效
@ComponentScan(basePackages = {"com.ttt.one.common.exception", "com.ttt.one.waiguagg"})
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class OneWaiguaggApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneWaiguaggApplication.class, args);
    }

}
