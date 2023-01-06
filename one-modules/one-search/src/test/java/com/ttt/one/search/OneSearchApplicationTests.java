package com.ttt.one.search;

import com.alibaba.fastjson.JSON;
import com.ttt.one.search.config.MyElasticsearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class OneSearchApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private RestHighLevelClient client;

    /**
     * waiguaId
     *
     * waiguaInfoId
     *
     *  waiguaUsername
     *
     *  waiguaType
     *
     * waiguaDescribe
     *
     *  createTime
     *
     *  location
     *
     * 测试查询es数据
     */
    @Test
    void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("users");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("gender","nan"));
        System.out.println("条件="+searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        //执行检索
        SearchResponse searchResponse = client.search(searchRequest, MyElasticsearchConfig.COMMON_OPTIONS);
        System.out.println(searchResponse.toString());
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String s = searchHit.getSourceAsString();
            System.out.println("s=="+s);
            User user = JSON.parseObject(s, User.class);
            System.out.println(user);
        }
    }
    /**
     * 测试存储数据到es
     */
    @Test
    void indexData(){
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        //       indexRequest.source("userName","zhangsan","age","18","gender","男");

        User user = new User(18,"nan","zhangsan");
        String s = JSON.toJSONString(user);
        indexRequest.source(s, XContentType.JSON);//要保存的内容
        IndexResponse index = null;
        try {
            index = client.index(indexRequest, MyElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(index);
    }
    @Data
    static class User{
        private String userName;
        private String gender;
        private int age;

        public User(){

        }

        public User(int age,String userName, String gender) {
            this.userName = userName;
            this.gender = gender;
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "userName='" + userName + '\'' +
                    ", gender='" + gender + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
