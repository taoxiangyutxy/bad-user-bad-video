package com.ttt.one.waiguagg.feign;

import com.ttt.one.common.utils.R;
import com.ttt.one.common.vo.UserEntity;
import com.ttt.one.waiguagg.config.FeignTokenInterceptor;
import com.ttt.one.waiguagg.feign.fallback.AdminUserFeignServerFallback;
import com.ttt.one.waiguagg.feign.fallback.UserFeignServerFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 后台管理模块 调用服务需要传参请求头token
 */
@FeignClient(value = "one-admin",configuration = FeignTokenInterceptor.class,
        fallback = AdminUserFeignServerFallback.class)
public interface AdminUserFeignServer {
    @RequestMapping("/api/admin/get-current-user")
     R getCurrentUser();

}