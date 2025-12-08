package com.ttt.one.waiguagg.feign;

import com.ttt.one.common.utils.R;
import com.ttt.one.common.vo.UserEntity;
import com.ttt.one.waiguagg.feign.fallback.UserFeignServerFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户模块
 */
@FeignClient(value = "one-user",fallback = UserFeignServerFallback.class)
public interface UserFeignServer {
    @RequestMapping("/user/user/info/{id}")
     R info(@PathVariable("id") Long id);

    @PostMapping("/user/user/update")
     R update(@RequestBody UserEntity user);
}