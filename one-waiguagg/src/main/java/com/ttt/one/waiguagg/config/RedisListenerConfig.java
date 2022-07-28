package com.ttt.one.waiguagg.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 *
 * 定义配置 RedisListenerConfig 实现监听 Redis key 过期时间
 */
@Configuration
public class RedisListenerConfig {

    @Bean
    @Primary //找到了两个一样的bean   将这个设置为主要的
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}