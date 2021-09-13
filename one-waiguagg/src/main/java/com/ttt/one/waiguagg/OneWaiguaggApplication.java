package com.ttt.one.waiguagg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.ttt.one.waiguagg.dao")
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class OneWaiguaggApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneWaiguaggApplication.class, args);
    }

}
