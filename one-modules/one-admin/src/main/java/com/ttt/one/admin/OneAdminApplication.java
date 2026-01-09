package com.ttt.one.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.ttt.one.admin.dao")
//使全局异常处理器在该服务生效
//@ComponentScan(basePackages = {"com.ttt.one.admin.exception", "com.ttt.one.admin"})
@EnableDiscoveryClient
@SpringBootApplication
public class OneAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneAdminApplication.class, args);
    }

}
