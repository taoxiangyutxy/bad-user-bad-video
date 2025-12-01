package com.ttt.one.waiguagg.listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * 监听所有db的过期事件__keyevent@*__:expired"
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    private Logger logger = LoggerFactory.getLogger(RedisKeyExpirationListener.class);
    @Autowired
    private StringRedisTemplate redisTemplate;
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }
    /**
     * 针对 redis 数据失效事件，进行数据处理
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        logger.error("监听redis失效时间");
        // 获取到失效的 key，进行取消订单业务处理
        String expiredKey = message.toString();
       // String s = redisTemplate.opsForValue().get("allWaiGuaData");
        logger.error("redis过期key:{};过期key的值:{}",expiredKey);

    }
}
