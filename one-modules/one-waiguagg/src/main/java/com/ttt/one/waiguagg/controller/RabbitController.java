package com.ttt.one.waiguagg.controller;

import com.ttt.one.waiguagg.entity.InfoEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
@Tag(name = "RabbitMQ测试")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rabbit")
public class RabbitController {
    private final RabbitTemplate rabbitTemplate;
    @Operation(summary = "发送消息")
    @RequestMapping("/sendMessage")
    public String sendMessageTest(){
        for (int i = 0; i < 5; i++) {
            InfoEntity infoEntity = new InfoEntity();
            infoEntity.setId(1L);
            infoEntity.setCreateTime(new Date());
            infoEntity.setWaiguaDescribe("测试消息接收对象"+i);
            String msg ="你好！世界."+i;
            rabbitTemplate.convertAndSend("hello-java-exchange",msg+"hello.java",infoEntity);
            log.info("消息发送完成{}",infoEntity);
        }
        return "ok";
    }
}
