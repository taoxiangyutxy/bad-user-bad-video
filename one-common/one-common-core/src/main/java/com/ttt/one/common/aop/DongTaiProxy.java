package com.ttt.one.common.aop;

import org.springframework.cglib.proxy.Proxy;
import java.util.Arrays;

/**
 * 动态代理 日志打印
 */
public class DongTaiProxy {
    public static Object getDProxty(Object tager){
        return Proxy.newProxyInstance(tager.getClass().getClassLoader(), tager.getClass().getInterfaces(),
                (proxy, method, args)->{
                    System.out.println("日志开始"+method.getName()+"参数："+ Arrays.toString(args));
                    Object result = null;
                    try {
                        result = method.invoke(tager, args);
                        System.out.println("日志结果"+method.getName()+"结果："+result);
                    } catch (Exception e) {
                        System.out.println("日志异常"+method.getName()+"异常："+e.getCause());
                        e.printStackTrace();
                    }
                    System.out.println("日志结束"+method.getName());
                    return result;
                });
    }
}
