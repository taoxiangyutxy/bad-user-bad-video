package com.ttt.one.auth.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ttt.one.auth.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@PropertySource(value = "classpath:application-jwt.properties")
public class TokenUtil {

    @Value("${token.expire.time}")
    private long tokenExpireTime;

    @Value("${refresh.token.expire.time}")
    private long refreshTokenExpireTime;

    private Map<String , String> map = new HashMap<>(2);

    /**
     * 固定的头
     */
    private static final String OPERATE = "OPERATE";
    private static final String USER = "USER";
    private static final String WX = "WX";

    private ResponseDto responseDto;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成token和refreshToken
     * @param phone
     * @param type
     * @return
     */
    public Map<String, String> getToken(String phone, String type){
        //生成refreshToken
        String refreshToken = UUID.randomUUID().toString().replaceAll("-","");
        String prefix = this.getPrefix(type);
        String token = this.buildJWT(phone, prefix);
        String key = SecureUtil.md5(prefix + phone);
        //向hash中放入数值
        stringRedisTemplate.opsForHash().put(key,"token", token);
        stringRedisTemplate.opsForHash().put(key,"refreshToken", refreshToken);
        //设置key过期时间
        stringRedisTemplate.expire(key,
                refreshTokenExpireTime, TimeUnit.MILLISECONDS);
        map.put("token", token);
        map.put("refreshToken", refreshToken);
        return map;
    }

    /**
     * 刷新token
     * @param phone
     * @param type
     * @param refreshToken
     * @return
     */
    public ResponseDto refreshToken(String phone, String type, String refreshToken){
        String prefix = this.getPrefix(type);
        String key = SecureUtil.md5(prefix + phone);
        String oldRefresh = (String) stringRedisTemplate.opsForHash().get(key, "refreshToken");
        if (StrUtil.isBlank(oldRefresh)){
            responseDto = new ResponseDto(1,"refreshToken过期",null);
        }else {
            if (!oldRefresh.equals(refreshToken)){
                responseDto = new ResponseDto(2,"refreshToken错误",null);
                System.out.println("refreshToken错误");
            }else {
                String token = this.buildJWT(phone, prefix);
                stringRedisTemplate.opsForHash().put(key,"token", token);
                stringRedisTemplate.opsForHash().put(key,"refreshToken", refreshToken);
                stringRedisTemplate.expire(key,
                        refreshTokenExpireTime, TimeUnit.MILLISECONDS);
                responseDto = new ResponseDto(0,"成功并返回数据", token, refreshToken);
            }
        }
        return responseDto;
    }

    /**
     * 删除key
     * @param phone
     * @param type
     */
    public boolean removeToken(String phone, String type){
        String prefix = this.getPrefix(type);
        String key = SecureUtil.md5(prefix + phone);
        return stringRedisTemplate.delete(key);
    }

    /**
     * 获取前缀
     * @param type 1 操作端  2  用户端 3 小程序
     * @return
     */
    private String getPrefix(String type){
        String prefix = null;
        if ("1".equals(type)){
            prefix =OPERATE;
        }else if ("2".equals(type)){
            prefix = USER;
        }else if ("3".equals(type)){
            prefix =WX;
        }
        return prefix;
    }

    /**
     * 生成jwt
     * @param phone 手机号
     * @param prefix 前缀
     * @return
     */
    private String buildJWT(String phone, String prefix){
        //生成jwt
        Date now = new Date();
        Algorithm algo = Algorithm.HMAC256(prefix);
        String token = JWT.create()
                //签发人
                .withIssuer("userPhone")
                //签发时间
                .withIssuedAt(now)
                //过期时间
                .withExpiresAt(new Date(now.getTime() + tokenExpireTime))
                //自定义的存放的数据
                .withClaim("phone", phone)
                //签名
                .sign(algo);
        return token;
    }
}

