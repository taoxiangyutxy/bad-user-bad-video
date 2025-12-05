package com.ttt.one.waiguagg.feign.fallback;

import com.ttt.one.common.utils.R;
import com.ttt.one.waiguagg.feign.FileServer;
import com.ttt.one.waiguagg.vo.FileInfoVO;
import org.springframework.stereotype.Component;

@Component
public class FileServerFallback implements FileServer {
    @Override
    public R deleAllIn(Long infoId) {
        return R.error(500, "文件服务异常");
    }

    @Override
    public R videoInfo(Long id) {
        return R.error(500, "文件服务异常");
    }

    @Override
    public R updateFileInfoByWeb(FileInfoVO infoVO) {
        return R.error(500, "文件服务异常");
    }
}
