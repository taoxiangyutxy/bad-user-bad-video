package com.ttt.one.fileServer.config;
import com.rabbitmq.client.Channel;
import com.ttt.one.fileServer.entity.FileInfoEntity;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * MQ 延迟队列 配置类  注意： 如果队列或者交换机有已创建好的重名的  则使用已有的 - 监听消息方法 发现都没有队列 才会自动创建队列
 */
@Configuration
public class MyMQConfig {
    /**
     * 监听释放队列
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitListener(queues = "hello.release.queue")
    public void listener(FileInfoEntity entity, Channel channel, Message message) throws IOException {
        System.out.println("收到过期的订单信息：准备关闭订单=;"+entity.toString());
       // channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
    /**
     * 延迟队列
     * @return
     */
    @Bean
    public Queue delayQueue(){
        System.out.println("初始化delayQueue");
        Map<String, Object> arguments = new HashMap<>();
        /**
         * 死信队列属性
         * x-dead-letter-exchange: user.order.exchange   指定死信路由
         * x-dead-letter-routing-key: order     死信路由键-路由给释放队列
         * x-message-ttl: 60000                消息过期时间（单位毫秒）
         */
        arguments.put("x-dead-letter-exchange","topic-exchange");
        arguments.put("x-dead-letter-routing-key","release.ttt");
        arguments.put("x-message-ttl",60000);
        //全参构造 String name, 队列名称
        // boolean durable,     是不是持久化的
        // boolean exclusive,   是不是排他的
        // boolean autoDelete,  是不是自动删除的
        // @Nullable Map<String, Object> arguments  自定义属性
        Queue queue = new Queue("hello.delay.queue",true,false,false,arguments);
        return queue;
    }
    /**
     * 释放队列
     * @return
     */
    @Bean
    public Queue releaseQueue(){
        System.out.println("初始化releaseQueue");
        return new Queue("hello.release.queue",true,false,false);
    }
    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange eventExchange(){
        //String name, 名称
        // boolean durable, 是不是持久化
        // boolean autoDelete, 是不是自动删除
        // Map<String, Object> arguments
       return  new TopicExchange("topic-exchange",true,false);
    }

    /**
     * 绑定关系1  绑定延时队列与交换机
     * @return
     */
    @Bean
    public Binding createBinding(){
        //String destination, 目的地队列
        // Binding.DestinationType destinationType,  目的地类型
        // String exchange, 交换机
        // String routingKey, 路由键
        // @Nullable Map<String, Object> arguments
       return new Binding("hello.delay.queue",
                Binding.DestinationType.QUEUE,
                "topic-exchange",
                "create.ttt",null);
    }
    /**
     * 绑定关系2  绑定释放队列与交换机
     * @return
     */
    @Bean
    public Binding releaseBinding(){
        System.out.println("初始化releaseBinding");
        //String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
        return new Binding("hello.release.queue",
                Binding.DestinationType.QUEUE,
                "topic-exchange",
                "release.ttt",null);
    }
}
