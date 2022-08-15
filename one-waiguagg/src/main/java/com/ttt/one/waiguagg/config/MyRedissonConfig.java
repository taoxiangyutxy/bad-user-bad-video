package com.ttt.one.waiguagg.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * redisson 分布式锁
 */
@Configuration
public class MyRedissonConfig {
    @Value("${spring.ttt.hostIp}")
    private String hostIp;
    /**
     * 所有对redisson的使用都是通过RedissonClient
     *  destroyMethod = "shutdown" 服务停止以后调用该方法销毁
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        //创建配置
        Config config = new Config();
        //集群方式
        /*config.useClusterServers()
                .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                //可以用"rediss://"来启用SSL连接
                .addNodeAddress("redis://127.0.0.1:7000", "redis://127.0.0.1:7001")
                .addNodeAddress("redis://127.0.0.1:7002");*/
        //单节点方式
        config.useSingleServer().setAddress(""+hostIp+":6379");
        RedissonClient redissonClient = Redisson.create(config);
        return  redissonClient;
    }
    
}
