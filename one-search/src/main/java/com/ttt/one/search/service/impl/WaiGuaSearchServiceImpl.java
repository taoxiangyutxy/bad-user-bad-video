package com.ttt.one.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.search.config.MyElasticsearchConfig;
import com.ttt.one.search.constant.EsConstant;
import com.ttt.one.search.service.WaiGuaSearchService;
import com.ttt.one.search.vo.SearchParam;
import com.ttt.one.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public SearchResult search(SearchParam param) {
        SearchResult result = null;
        //1 准备检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            //2 执行检索请求
            SearchResponse response = restHighLevelClient.search(searchRequest, MyElasticsearchConfig.COMMON_OPTIONS);
            //3 分析响应数据封装成需要的格式
            result = buildSearchResult(response,param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



    /**
     * 构造检索请求
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        //构建DSL语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /**
         * bool -  must模糊查询  描述（当标题）模糊查询和类型查询
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("waiguaDescribe",param.getKeyword()));
        }
        if(!StringUtils.isEmpty(param.getWaiguaType())){
            boolQuery.must(QueryBuilders.matchQuery("waiguaType",param.getWaiguaType()));
        }
        //bool - filter查询  waiguaUsername
        if(!StringUtils.isEmpty(param.getWaiguaUsername())){
            boolQuery.filter(QueryBuilders.termQuery("waiguaUsername",param.getWaiguaUsername()));
        }
        sourceBuilder.query(boolQuery);

        /**
         * 聚合分析  类型
         */
        TermsAggregationBuilder waiguaType_agg = AggregationBuilders.terms("waiguaType_agg");
        waiguaType_agg.field("waiguaType").size(6);
        sourceBuilder.aggregation(waiguaType_agg);

        /**
         * 排序 分页 高亮
         */
        //排序  sort  time_asc/desc
        if(!StringUtils.isEmpty(param.getSort())){
            String [] s = param.getSort().split("_");
            SortOrder sortOrder = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0],sortOrder);
        }
        //分页  form 从哪开始  size 每页显示几个
        sourceBuilder.from((param.getPageNum()-1)*EsConstant.WAIGUA_PAGESIZE);
        sourceBuilder.size(EsConstant.WAIGUA_PAGESIZE);
        //高亮 有模糊匹配才有高亮
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("waiguaDescribe");
            //前置标签
            builder.preTags("<b style = 'color:red'>");
            //后置标签
            builder.postTags("</b>");
            sourceBuilder.highlighter(builder);
        }
        System.out.println("构建的DSL语句="+sourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.WAIGUA_INDEX},sourceBuilder);
        return searchRequest;
    }

    /**
     * 构造结果返回
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response,SearchParam param) {
        SearchResult result = new SearchResult();
        //返回所有查到的数据
        SearchHits hits = response.getHits();
        List<WaiguaEsModel> esModels = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            String sourceAsString = hit.getSourceAsString();
            WaiguaEsModel esModel = JSON.parseObject(sourceAsString,WaiguaEsModel.class);
            //获取高亮信息
            if(!StringUtils.isEmpty(param.getKeyword())){
                HighlightField waiguaDescribe = hit.getHighlightFields().get("waiguaDescribe");
                String string = waiguaDescribe.getFragments()[0].string();
                esModel.setWaiguaDescribe(string);
            }
            esModels.add(esModel);
        }
        result.setEsModels(esModels);

        //类型聚合结果
        List<String> waiguaTypes = new ArrayList<>();
        ParsedStringTerms waiguaType_agg = response.getAggregations().get("waiguaType_agg");
        List<? extends Terms.Bucket> buckets = waiguaType_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String keyAsString = bucket.getKeyAsString();
            waiguaTypes.add(keyAsString);
        }
        result.setWaiguaTypes(waiguaTypes);

        //分页信息-页码
        result.setPageNum(param.getPageNum());
        //分页信息-总记录数
        long total =hits.getTotalHits().value;
        result.setTotal(total);
        //分页信息-总页码
        int totalPages = (int)total%EsConstant.WAIGUA_PAGESIZE == 0?(int)total/EsConstant.WAIGUA_PAGESIZE:((int)total/EsConstant.WAIGUA_PAGESIZE)+1;
        result.setTotalPages(totalPages);

        return result;
    }
}
