package com.ttt.one.waiguagg.fegin;

import com.ttt.one.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient("renren-fast")
public interface UserFeginServer {
    @GetMapping("/sys/user/infoById/{userId}")
    public R infoById(@PathVariable("userId") Long userId);
}
