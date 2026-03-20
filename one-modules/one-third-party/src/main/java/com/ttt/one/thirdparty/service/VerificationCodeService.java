package com.ttt.one.thirdparty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 */
@Service
public class VerificationCodeService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String REDIS_KEY_PREFIX = "verification_code:";
    private static final int CODE_EXPIRE_MINUTES = 15; // 验证码过期时间（分钟）
    private static final int MAX_REQUESTS_PER_HOUR = 3; // 每小时最大请求次数
    private static final String REQUEST_COUNT_PREFIX = "request_count:";

    /**
     * 生成6位数字验证码
     *
     * @return 6位数字验证码
     */
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成100000-999999之间的随机数
        return String.valueOf(code);
    }

    /**
     * 存储验证码到Redis
     *
     * @param key   存储键
     * @param code  验证码
     */
    public void storeVerificationCode(String key, String code) {
        redisTemplate.opsForValue().set(
            REDIS_KEY_PREFIX + key, 
            code, 
            CODE_EXPIRE_MINUTES, 
            TimeUnit.MINUTES
        );
    }

    /**
     * 验证验证码
     *
     * @param key   存储键
     * @param code  验证码
     * @return 验证结果
     */
    public boolean verifyCode(String key, String code) {
        String storedCode = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + key);
        if (storedCode != null && storedCode.equals(code)) {
            // 验证成功后删除验证码
            redisTemplate.delete(REDIS_KEY_PREFIX + key);
            return true;
        }
        return false;
    }

    /**
     * 检查是否可以发送验证码（频率限制）
     *
     * @param key 存储键
     * @return 是否可以发送
     */
    public boolean canSendCode(String key) {
        String countKey = REQUEST_COUNT_PREFIX + key;
        String countStr = redisTemplate.opsForValue().get(countKey);
        
        if (countStr == null) {
            // 首次请求，设置计数器
            redisTemplate.opsForValue().set(countKey, "1", 1, TimeUnit.HOURS);
            return true;
        }
        
        int count = Integer.parseInt(countStr);
        if (count >= MAX_REQUESTS_PER_HOUR) {
            return false;
        }
        
        // 增加请求计数
        redisTemplate.opsForValue().increment(countKey, 1);
        return true;
    }

    /**
     * 重置请求计数
     *
     * @param key 存储键
     */
    public void resetRequestCount(String key) {
        redisTemplate.delete(REQUEST_COUNT_PREFIX + key);
    }
}