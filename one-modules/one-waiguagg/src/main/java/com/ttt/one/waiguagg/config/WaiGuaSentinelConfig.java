package com.ttt.one.waiguagg.config;

//import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
//import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
//import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.ttt.one.common.exception.BizCodeEnum;
import com.ttt.one.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**\
 *Sentinel 配置类
 */
//@Configuration
public class WaiGuaSentinelConfig {
    public WaiGuaSentinelConfig(){
//        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
//            @Override
//            public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
//                R error = R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
//                httpServletResponse.setCharacterEncoding("UTF-8");
//                httpServletResponse.setContentType("application/json");
//                httpServletResponse.getWriter().write(JSON.toJSONString(error));
//            }
//        });
    }
}
