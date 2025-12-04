package com.ttt.one.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.ttt.one.common.to.es.OperationLogInfo;
import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.search.config.MyElasticsearchConfig;
import com.ttt.one.search.constant.EsConstant;
import com.ttt.one.search.service.WaiGuaSearchService;
import com.ttt.one.search.vo.LogSearchParam;
import com.ttt.one.search.vo.LogSearchResult;
import com.ttt.one.search.vo.SearchParam;
import com.ttt.one.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class WaiGuaSearchServiceImpl implements WaiGuaSearchService {
    @Autowired
    private  RestHighLevelClient restHighLevelClient;
    //批量操作的对象
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

    @Override
    public void waiguaInfoBatchUpdate(List<WaiguaEsModel> esModelList) {
        //updateByWhere();
        //TODO 搞成批量修改
      //  List<UpdateRequest> updateRequests=new ArrayList<>();
        //更新的数据
        esModelList.forEach(e->{
            //获取id
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(EsConstant.WAIGUA_INDEX);
            //更新的id
            updateRequest.id(e.getInfoId()+"");
            //更新的数据
            Map<String,Object> map=new HashMap<>();
            map.put("location",e.getLocation());
            map.put("createTime",e.getCreateTime());
            updateRequest.doc(map);
           // updateRequests.add(updateRequest);
            try {
                restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        //updateRequests.forEach(bulkProcessor::add);
    }

    @Override
    public boolean operationLogSaveES(OperationLogInfo logInfo) {
        //1.ES中建立索引，建立好映射关系 waiguas_mapping.txt
        //2.往ES中保存数据
       /* String index = logInfo.getApplicationName();
        if("auth".equals(index)){
            index = EsConstant.ONE_AUTH_SERVER_INDEX;
        }*/
        //共用这一个index   根据表名去区分各个服务的接口；  否则先去ES中建立索引及映射关系。
        String index = EsConstant.ONE_AUTH_SERVER_INDEX;
        logInfo.setExtend1(UUID.randomUUID().toString());
        BulkRequest bulkRequest = new BulkRequest();
        IndexRequest indexRequest = new IndexRequest(index);
        //唯一值
        indexRequest.id(logInfo.getExtend1());
        String s = JSON.toJSONString(logInfo);
        indexRequest.source(s, XContentType.JSON);
        bulkRequest.add(indexRequest);
        //批量存入
        BulkResponse bulk = null;
        try {
            bulk = restHighLevelClient.bulk(bulkRequest, MyElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //是否有错误  如果没错误返回false  ，有错误返回true
        boolean b = bulk.hasFailures();
        BulkItemResponse[] items = bulk.getItems();
        log.info("存入ES完成infoID：{}",items[0].getId());
        return b;
    }

    @Override
    public LogSearchResult searchLog(LogSearchParam logSearchParam) {
        LogSearchResult result = null;
        //1 准备检索请求
        SearchRequest searchRequest = buildLogSearchRequest(logSearchParam);
        try {
            //2 执行检索请求
            SearchResponse response = restHighLevelClient.search(searchRequest, MyElasticsearchConfig.COMMON_OPTIONS);
            //3 分析响应数据封装成需要的格式
            result = buildLogSearchResult(response,logSearchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *  描述:  根据条件修改  成功的
     * @param :
     * @return void
     * @author txy
     * @description
     * @date 2021/11/19 15:26
     */
    private void updateByWhere() {
        UpdateByQueryRequest updateByQuery  = new UpdateByQueryRequest(EsConstant.WAIGUA_INDEX);
        //设置分片并行
        updateByQuery.setSlices(2);
        //设置版本冲突时继续执行
        updateByQuery.setConflicts("proceed");
        //设置更新完成后刷新索引 ps很重要如果不加可能数据不会实时刷新
        updateByQuery.setRefresh(true);
        //查询条件如果是and关系使用must 如何是or关系使用should
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("infoId","24"));
        updateByQuery.setQuery(boolQueryBuilder);
        //设置要修改的内容可以多个值多个用；隔开
        updateByQuery.setScript(new Script("ctx._source['location']='北京'"));
        try {
            BulkByScrollResponse response= restHighLevelClient.updateByQuery(updateByQuery, RequestOptions.DEFAULT);
            boolean b = response.getStatus().getUpdated() > 0 ? true : false;
            System.out.println("************************************"+b);
        } catch (IOException e) {
            e.printStackTrace();
        }
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



    private SearchRequest buildLogSearchRequest(LogSearchParam param) {
        //构建DSL语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

     /*   // 添加 term 查询
        TermQueryBuilder termQuery = QueryBuilders.termQuery("field_name", "value_to_search");
        sourceBuilder.query(termQuery);*/
        /**
         * bool -  must模糊查询  描述（当标题）模糊查询和类型查询
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 添加 term 精确查询到 bool 查询中
        if(!StringUtils.isEmpty(param.getTableName())) {
            boolQuery.must(QueryBuilders.termQuery("tableName", param.getTableName()));
        }
        if(!StringUtils.isEmpty(param.getMethodName())) {
            boolQuery.must(QueryBuilders.termQuery("methodName", param.getMethodName()));
        }
        // 添加match 模糊查询
        if(!StringUtils.isEmpty(param.getParam())){
            boolQuery.must(QueryBuilders.matchQuery("param",param.getParam()));
        }
        if(!StringUtils.isEmpty(param.getError())){
            boolQuery.must(QueryBuilders.matchQuery("error",param.getError()));
        }
        if(!StringUtils.isEmpty(param.getResult())){
            boolQuery.must(QueryBuilders.matchQuery("result",param.getResult()));
        }
        if(!StringUtils.isEmpty(param.getUserName())){
            boolQuery.must(QueryBuilders.matchQuery("userName",param.getUserName()));
        }
        if(!StringUtils.isEmpty(param.getRealName())){
            boolQuery.must(QueryBuilders.matchQuery("realName",param.getRealName()));
        }
        if(!StringUtils.isEmpty(param.getMemberName())){
            boolQuery.must(QueryBuilders.matchQuery("memberName",param.getMemberName()));
        }
        //bool - filter查询
        if(!ObjectUtils.isEmpty(param.getStartTime()) && !ObjectUtils.isEmpty(param.getEndTime())){
            Instant startDate = Instant.ofEpochSecond(param.getStartTime().getTime()); // 例如：2021-07-11T00:00:00Z
            Instant endDate = Instant.ofEpochSecond(param.getEndTime().getTime());   // 例如：2021-07-12T00:00:00Z - 1 second
            boolQuery.filter(QueryBuilders.rangeQuery("opTime").gte(startDate).lte(endDate));
        }
        sourceBuilder.query(boolQuery);
        /**
         * 排序 分页 高亮
         */
        //排序  sort  time_asc/desc
        param.setSort("opTime_desc");
        if(!StringUtils.isEmpty(param.getSort())){
            String [] s = param.getSort().split("_");
            SortOrder sortOrder = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0],sortOrder);
        }
        //分页  form 从哪开始  size 每页显示几个
        sourceBuilder.from((param.getPageNum()-1)*EsConstant.WAIGUA_PAGESIZE);
        sourceBuilder.size(EsConstant.WAIGUA_PAGESIZE);
        System.out.println("构建的DSL语句="+sourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.ONE_AUTH_SERVER_INDEX},sourceBuilder);
        return searchRequest;
    }

    /**
     * 构造结果返回
     * @param response
     * @return
     */
    private LogSearchResult buildLogSearchResult(SearchResponse response,LogSearchParam param) {
        // 创建一个SimpleDateFormat对象，设置日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LogSearchResult result = new LogSearchResult();
        //返回所有查到的数据
        SearchHits hits = response.getHits();
        List<OperationLogInfo> logInfos = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            String sourceAsString = hit.getSourceAsString();
            OperationLogInfo logInfo = JSON.parseObject(sourceAsString,OperationLogInfo.class);
            logInfo.setDateStr(sdf.format(logInfo.getOpTime()));
            logInfos.add(logInfo);
        }
        result.setLogInfos(logInfos);
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
