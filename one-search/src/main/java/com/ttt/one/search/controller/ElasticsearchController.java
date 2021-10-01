package com.ttt.one.search.controller;

import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.R;
import com.ttt.one.search.service.WaiGuaSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@Slf4j
@RestController
@RequestMapping("search")
public class ElasticsearchController {
    @Autowired
    private WaiGuaSearchService waiGuaSearchService;
    @RequestMapping("/waiguaInfoSaveES")
   public R waiguaInfoSaveES(@RequestBody WaiguaEsModel esModel)  {
        boolean b = false;
        try {
             b = waiGuaSearchService.waiguaInfoSaveEs(esModel);
        } catch (IOException e) {
            log.error("数据存入ES异常:{}",e);
            return R.error("数据存入ES异常");
        }
        if(!b){ return R.ok();}
        else {return  R.error("数据存入ES异常");}
   }
}
