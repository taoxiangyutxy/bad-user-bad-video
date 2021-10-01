package com.ttt.one.waiguagg.fegin;

import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
@FeignClient("one-search")
public interface EsSearchFeginServer {
    @RequestMapping("/search/waiguaInfoSaveES")
     R waiguaInfoSaveES(@RequestBody WaiguaEsModel esModel);
}
