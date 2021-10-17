package com.ttt.one.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OneUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(OneUserApplication.class, args);
    }

}
