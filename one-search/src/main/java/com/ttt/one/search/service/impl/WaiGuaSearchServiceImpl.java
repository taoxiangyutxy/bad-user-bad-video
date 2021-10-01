package com.ttt.one.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.search.config.MyElasticsearchConfig;
import com.ttt.one.search.constant.EsConstant;
import com.ttt.one.search.service.WaiGuaSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Slf4j
@Service
public class WaiGuaSearchServiceImpl implements WaiGuaSearchService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Override
    public boolean waiguaInfoSaveEs(WaiguaEsModel esModel) throws IOException {
        //1.ES中建立索引，建立好映射关系 waiguas_mapping.txt
        //2.往ES中保存数据
        BulkRequest bulkRequest = new BulkRequest();
        IndexRequest indexRequest = new IndexRequest(EsConstant.WAIGUA_INDEX);
        indexRequest.id(esModel.getInfoId().toString());
        String s = JSON.toJSONString(esModel);
        indexRequest.source(s, XContentType.JSON);
        bulkRequest.add(indexRequest);
        //批量存入
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, MyElasticsearchConfig.COMMON_OPTIONS);
        //是否有错误  如果没错误返回false  ，有错误返回true
        boolean b = bulk.hasFailures();
        BulkItemResponse[] items = bulk.getItems();
        log.info("存入ES完成infoID：{}",items[0].getId());
        return b;
    }
}
