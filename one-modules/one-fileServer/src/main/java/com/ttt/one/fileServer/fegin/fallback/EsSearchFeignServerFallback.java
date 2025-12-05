package com.ttt.one.fileServer.fegin.fallback;

import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.R;
import com.ttt.one.fileServer.fegin.EsSearchFeignServer;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class EsSearchFeignServerFallback implements EsSearchFeignServer {
    @Override
    public R waiguaInfoBatchUpdate(List<WaiguaEsModel> esModelList) {
        return R.error(500, "ES服务异常");
    }
}
