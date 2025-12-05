package com.ttt.one.fileServer.fegin;

import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.R;
import com.ttt.one.fileServer.fegin.fallback.EsSearchFeignServerFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "one-search",fallback = EsSearchFeignServerFallback.class)
public interface EsSearchFeignServer {

    @RequestMapping("/search/waiguaInfoBatchUpdate")
    public R waiguaInfoBatchUpdate(@RequestBody(required = false) List<WaiguaEsModel> esModelList);
}
