package com.ttt.one.waiguagg.config;


import com.ttt.one.waiguagg.utils.TokenUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * feign拦截器  获取当前请求token，并设置到请求头中
 */
@Component
@Slf4j
public class FeignTokenInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        try {
            // 1. 获取Token
            String token = getToken();
            if (StringUtils.isNotBlank(token)) {
                    // 3. 设置Token到请求头
                    requestTemplate.header("Authorization", "Bearer " + token);
                    log.debug("Feign请求设置Token: {}", maskToken(token));
            } else {
                log.warn("Feign请求未找到Token，请求路径: {}", requestTemplate.url());
            }
        } catch (Exception e) {
            log.error("Feign拦截器处理Token失败", e);
        }
    }
    
    /**
     * 获取Token
     */
    private String getToken() {
      String  token = TokenUtils.getTokenFromRequest();
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        return null;
    }

    /**
     * Token脱敏显示
     */
    private String maskToken(String token) {
        if (StringUtils.isBlank(token) || token.length() <= 8) {
            return "***";
        }
        return token.substring(0, 4) + "****" + token.substring(token.length() - 4);
    }
}