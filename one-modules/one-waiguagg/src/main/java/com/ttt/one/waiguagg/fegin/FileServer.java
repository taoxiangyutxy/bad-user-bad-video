package com.ttt.one.waiguagg.fegin;

import com.ttt.one.common.utils.R;
import com.ttt.one.waiguagg.vo.FileInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("one-fileServer")
public interface FileServer {

    @PostMapping("/fileServer/uploader/deleAllIn")
     R deleAllIn(@RequestParam("infoId") Long infoId);

    @RequestMapping("/fileServer/uploader/info/{id}")
     R videoInfo(@PathVariable("id") Long id);
    /**
     *  描述: 关联外挂信息表
     * @param infoVO: 根据视频唯一标识
     * @return R
     * @author txy
     * @description
     * @date 2021/12/24 16:58
     */
    @PostMapping("/fileServer/uploader/updateFileInfoByWeb")
     R updateFileInfoByWeb(@RequestBody FileInfoVO infoVO);
}
