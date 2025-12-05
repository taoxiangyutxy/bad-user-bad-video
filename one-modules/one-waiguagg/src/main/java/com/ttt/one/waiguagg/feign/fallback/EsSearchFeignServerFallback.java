package com.ttt.one.waiguagg.feign.fallback;

import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.R;
import com.ttt.one.waiguagg.feign.EsSearchFeignServer;
import org.springframework.stereotype.Component;

@Component
public class EsSearchFeignServerFallback implements EsSearchFeignServer {
    @Override
    public R waiguaInfoSaveES(WaiguaEsModel esModel) {
        return R.error(500,"ES搜索服务异常");
    }
}
