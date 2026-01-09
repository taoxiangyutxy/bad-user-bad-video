package com.ttt.one.waiguagg.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
public class TokenUtils {
    
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * 从当前请求中获取Token
     */
    public static String getTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
            RequestContextHolder.getRequestAttributes();
        
        if (attributes == null) {
            return null;
        }
        
        HttpServletRequest request = attributes.getRequest();
        return getTokenFromRequest(request);
    }
    
    /**
     * 从HttpServletRequest中获取Token
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        // 1. 首先尝试从Authorization头获取
        String authHeader = request.getHeader(TOKEN_HEADER);
        String token = extractTokenFromHeader(authHeader);
        
        if (token != null) {
            return token;
        }
        
        // 2. 尝试从参数中获取
        token = request.getParameter("token");
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        
        // 3. 尝试从X-Access-Token头获取
        token = request.getHeader("X-Access-Token");
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        
        // 4. 尝试从Cookie中获取
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
    
    /**
     * 从认证头中提取Token
     */
    public static String extractTokenFromHeader(String authHeader) {
        if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

}