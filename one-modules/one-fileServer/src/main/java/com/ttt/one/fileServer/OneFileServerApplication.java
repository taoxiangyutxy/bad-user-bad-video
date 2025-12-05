package com.ttt.one.fileServer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRabbit
@EnableScheduling //启动定时任务调度
@MapperScan(basePackages = {"com.ttt.one.fileServer.dao"})
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class OneFileServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OneFileServerApplication.class,args);
    }
}
