package com.ttt.one.auth;

import com.ttt.one.oplog.config.OperationLogAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession//整合redis作为session存储
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.ttt.one.auth", "com.ttt.one.thirdparty"})
//@Import(OperationLogAutoConfiguration.class)
//@ComponentScan(basePackages = {"com.ttt.one.oplog","com.ttt.one.auth"})
public class OneAuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OneAuthServerApplication.class, args);
    }

}
