package com.ttt.one.waiguagg.feign;

import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.R;
import com.ttt.one.waiguagg.feign.fallback.EsSearchFeignServerFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
@FeignClient(value = "one-search",fallback = EsSearchFeignServerFallback.class)
public interface EsSearchFeignServer {
    @RequestMapping("/search/waiguaInfoSaveES")
     R waiguaInfoSaveES(@RequestBody WaiguaEsModel esModel);
}
