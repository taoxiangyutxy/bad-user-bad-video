package com.ttt.one.waiguagg.fegin;

import com.ttt.one.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户模块
 */
@FeignClient("one-user")
public interface UserFeginServer {
    @RequestMapping("/user/user/info/{id}")
     R info(@PathVariable("id") Long id);
}