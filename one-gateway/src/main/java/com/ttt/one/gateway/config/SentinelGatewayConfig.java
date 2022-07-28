package com.ttt.one.gateway.config;

//import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
//import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.ttt.one.common.exception.BizCodeEnum;
import com.ttt.one.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author   SentinelGateway配置类
 * @date 2021/3/30 14:40
 * @description
 */
//@Configuration
public class SentinelGatewayConfig {

    //TODO 响应式编程
 //   public SentinelGatewayConfig(){
//        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
//            //网关限流了请求，就会调用此回调 Mono Flux
//            @Override
//            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
//                R error = R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
//                String errorJson = JSON.toJSONString(error);
//                Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(errorJson), String.class);
//                return body;
//            }
//        });
//    }
}
