package com.ttt.one.search.controller;

import com.ttt.one.common.to.es.OperationLogInfo;
import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.R;
import com.ttt.one.search.service.WaiGuaSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("search")
public class ElasticsearchController {
    @Autowired
    private WaiGuaSearchService waiGuaSearchService;
    /**
     *  描述: 单条插入ES
     * @param esModel:
     * @return R
     * @author txy
     * @description
     * @date 2021/11/19 14:26
     */
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

    /**
     *  描述:  根据条件 更新ES库里的链接  每6天更新一次链接
     * @param esModelList:
     * @return R
     * @author txy
     * @description
     * @date 2021/11/19 16:58
     */
    @RequestMapping("/waiguaInfoBatchUpdate")
    public R waiguaInfoBatchUpdate(@RequestBody(required = false) List<WaiguaEsModel> esModelList){
        waiGuaSearchService.waiguaInfoBatchUpdate(esModelList);
        return R.ok();
    }


    @RequestMapping("/operationLogSaveES")
    public R operationLogSaveES(@RequestBody OperationLogInfo logInfo)  {
        boolean b = false;
        try {
            b = waiGuaSearchService.operationLogSaveES(logInfo);
        } catch (Exception e) {
            log.error("数据存入ES异常:{}",e);
            return R.error("数据存入ES异常");
        }
        if(!b){ return R.ok();}
        else {return  R.error("数据存入ES异常");}
    }
}
