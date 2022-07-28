package com.ttt.one.waiguagg.controller;

import com.ttt.one.waiguagg.entity.InfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
@Slf4j
@RestController
@RequestMapping("/rabbit")
public class RabbitController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendMessage")
    public String sendMessageTest(){
        for (int i = 0; i < 5; i++) {
            InfoEntity infoEntity = new InfoEntity();
            infoEntity.setId(1L);
            infoEntity.setCreateTime(new Date());
            infoEntity.setWaiguaDescribe("测试消息接收对象"+i);
            String msg ="你好！世界."+i;
            rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",infoEntity);
            log.info("消息发送完成{}",infoEntity);
        }

        return "ok";
    }
}
