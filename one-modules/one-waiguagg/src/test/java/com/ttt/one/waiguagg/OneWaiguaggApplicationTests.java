package com.ttt.one.waiguagg;

import com.alibaba.fastjson.JSON;
import com.ttt.one.waiguagg.config.MyElasticsearchConfig;
import com.ttt.one.waiguagg.entity.InfoEntity;
import com.ttt.one.waiguagg.service.InfoService;
import com.ttt.one.waiguagg.service.UnmberService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;

@Slf4j
@SpringBootTest
class OneWaiguaggApplicationTests {
    @Autowired
    UnmberService service;

    @Autowired
    InfoService infoService;

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Test
    public void sendMessageTest(){
        InfoEntity infoEntity = new InfoEntity();
        infoEntity.setId(1L);
        infoEntity.setCreateTime(new Date());
        infoEntity.setWaiguaDescribe("测试消息接收对象444");
        String msg ="你好！世界.";
      //  rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",infoEntity);
        log.info("消息发送完成{}",infoEntity);

        rabbitTemplate.convertAndSend("topic-exchange","create.ttt",infoEntity);

    }
    /**
     * 1、如何创建 Exchange 、Queue、Binging
     *      a、使用AmqpAdmin进行创建
     * 2、如何收发消息
     *
     */
    @Test
    public void createExchange(){
        //全参构造 String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        DirectExchange exchange = new DirectExchange("hello-java-exchange",true,false);
        amqpAdmin.declareExchange(exchange);
        log.info("exchange[{}]创建完成","hello-java-exchange");
    }
    @Test
    public void createQueue(){
        //全参构造 String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
        Queue queue = new Queue("hello-java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("queue[{}]创建完成","hello-java-queue");
    }
    @Test
    public void createBinding(){
        //全参构造 String destination, 目的地
        // Binding.DestinationType destinationType,目的地类型
        // String exchange,交换机
        // String routingKey, 路由键
        // @Nullable Map<String, Object> arguments 自定义参数
        Binding binding = new Binding("hello-java-queue",Binding.DestinationType.QUEUE,
                "hello-java-exchange","hello2.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("binding[{}]创建完成","hello-java-binding");
    }


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
        SearchResponse searchResponse = client.search(searchRequest,MyElasticsearchConfig.COMMON_OPTIONS);
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
   //     indexRequest.source(s, XContentType.JSON);//要保存的内容
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
    @Test
    void contextLoads() {

        System.out.println(client);

     /*   UnmberEntity unmberEntity = new UnmberEntity();
        unmberEntity.setWaiguaUsername("rr221");
        service.save(unmberEntity);*/

    }

}
