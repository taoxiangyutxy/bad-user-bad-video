package com.ttt.one.waiguagg.interceptor;

import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.vo.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器- 需要web配置类 WaiguaWebConfiguration
 */
@Component
@RefreshScope
public class LoginUserInterceptor implements HandlerInterceptor {
    //登录获取到用户信息 放入本地线程  共享使用
    public static ThreadLocal<UserEntity> loginUser = new ThreadLocal<>();

    @Value("${spring.ttt.theHost}")
    private String theHost;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserEntity attribute = (UserEntity) request.getSession().getAttribute(Constant.LOGIN_USER);
        if(attribute!=null){
            loginUser.set(attribute);
            return  true;
        }else{
            //没登录  重定向去登录
            request.getSession().setAttribute("msg","请先进行登录!");
            response.sendRedirect("http://"+theHost+":88/one-auth-server/login/login.html");
            return false;
        }

    }
}
