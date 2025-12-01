package com.ttt.one.gateway.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局API过滤器 - JWT鉴权
 * 拦截所有请求进行Token验证，白名单内的URL不校验
 */
@Slf4j
@Component
@PropertySource(value = "classpath:application-jwt.properties")
public class ApiGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 不进行Token校验的请求地址白名单
     */
    @Value("#{'${jwt.ignoreUrlList}'.split(',')}")
    private List<String> ignoreUrlList;

    /**
     * Token请求头名称
     */
    private static final String TOKEN_HEADER = "token";

    /**
     * 类型请求头名称
     */
    private static final String TYPE_HEADER = "type";

    /**
     * 用户身份请求头名称
     */
    private static final String AUTH_USER_HEADER = "Authorization-UserName";

    /**
     * JWT签发者
     */
    private static final String JWT_ISSUER = "userPhone";

    /**
     * 用户类型前缀映射
     */
    private static final Map<String, String> TYPE_PREFIX_MAP = new HashMap<String, String>() {{
        put("1", "OPERATE");
        put("2", "USER");
        put("3", "WX");
    }};

    /**
     * 全局过滤器主方法
     * @param exchange 服务器Web交换对象
     * @param chain 过滤器链
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().toString();
        
        // 检查是否在白名单中
        if (isIgnoreUrl(requestUrl)) {
            log.debug("请求URL [{}] 在白名单中，跳过Token验证", requestUrl);
            return chain.filter(exchange);
        }
        
        // 获取Token和Type
        String token = exchange.getRequest().getHeaders().getFirst(TOKEN_HEADER);
        String type = exchange.getRequest().getHeaders().getFirst(TYPE_HEADER);
        
        // 验证Token和Type是否存在
        if (StrUtil.isBlank(token) || StrUtil.isBlank(type)) {
            log.warn("请求URL [{}] 缺少Token或Type", requestUrl);
            return buildErrorResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED, 
                    1, "鉴权失败，缺少Token或类型");
        }
        
        // 获取密钥前缀
        String prefix = getPrefix(type);
        if (prefix == null) {
            log.warn("请求URL [{}] 类型[{}]无效", requestUrl, type);
            return buildErrorResponse(exchange.getResponse(), HttpStatus.BAD_REQUEST, 
                    3, "无效的用户类型");
        }
        
        // 验证JWT Token
        String userPhone = verifyJWT(token, prefix);
        if (StrUtil.isEmpty(userPhone)) {
            log.warn("请求URL [{}] Token验证失败", requestUrl);
            return buildErrorResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED, 
                    2, "Token验证失败");
        }
        
        log.debug("请求URL [{}] Token验证成功，用户: {}", requestUrl, userPhone);
        
        // 将用户信息添加到请求头
        ServerHttpRequest mutableReq = exchange.getRequest().mutate()
                .header(AUTH_USER_HEADER, userPhone)
                .build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        
        return chain.filter(mutableExchange);
    }

    /**
     * 检查URL是否在白名单中
     * @param requestUrl 请求URL
     * @return true-在白名单中，false-不在白名单中
     */
    private boolean isIgnoreUrl(String requestUrl) {
        if (CollectionUtil.isEmpty(ignoreUrlList)) {
            return false;
        }
        return ignoreUrlList.stream()
                .anyMatch(ignoreUrl -> requestUrl.startsWith(ignoreUrl.trim()));
    }

    /**
     * 构建错误响应
     * @param response 响应对象
     * @param status HTTP状态码
     * @param code 业务错误码
     * @param message 错误消息
     * @return Mono<Void>
     */
    private Mono<Void> buildErrorResponse(ServerHttpResponse response, HttpStatus status, 
                                          int code, String message) {
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("message", message);
        
        byte[] bytes = result.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 验证JWT Token
     * @param token JWT Token
     * @param prefix 密钥前缀
     * @return 用户手机号，验证失败返回空字符串
     */
    private String verifyJWT(String token, String prefix) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(prefix);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(JWT_ISSUER)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("phone").asString();
        } catch (JWTVerificationException e) {
            log.error("JWT验证失败: {}", e.getMessage());
            return "";
        } catch (Exception e) {
            log.error("JWT验证异常", e);
            return "";
        }
    }

    /**
     * 根据用户类型获取密钥前缀
     * @param type 用户类型 (1-运营端, 2-用户端, 3-微信端)
     * @return 密钥前缀，无效类型返回null
     */
    private String getPrefix(String type) {
        return TYPE_PREFIX_MAP.get(type);
    }

    /**
     * 设置过滤器优先级
     * @return 优先级数值，数值越小优先级越高
     */
    @Override
    public int getOrder() {
        return -200;
    }
}
