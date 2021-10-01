package com.ttt.one.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OneSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneSearchApplication.class, args);
    }

}
