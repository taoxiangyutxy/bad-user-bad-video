package com.ttt.one.waiguagg.fegin;

import com.ttt.one.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("one-fileServer")
public interface FileServer {

    @PostMapping("/fileServer/uploader/deleAllIn")
    public R deleAllIn(@RequestParam Long infoId);

    @RequestMapping("/fileServer/uploader/info/{id}")
    public R videoInfo(@PathVariable("id") Long id);
}
