package com.ttt.one.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OneOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneOrderApplication.class, args);
    }

}
