package com.ttt.one.waiguagg.service.impl;

import com.rabbitmq.client.Channel;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;
import com.ttt.one.common.utils.R;
import com.ttt.one.waiguagg.entity.InfoEntity;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ttt.one.waiguagg.dao.UnmberDao;
import com.ttt.one.waiguagg.entity.UnmberEntity;
import com.ttt.one.waiguagg.service.UnmberService;
import org.springframework.transaction.annotation.Transactional;

//先创建队列
//@RabbitListener(queues = {"hello-java-queue"})
@Service("unmberService")
public class UnmberServiceImpl extends ServiceImpl<UnmberDao, UnmberEntity> implements UnmberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UnmberEntity> page = this.page(
                new Query<UnmberEntity>().getPage(params),
                new QueryWrapper<UnmberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAndInfoAndVideo(UnmberEntity unmber) {
        this.baseMapper.insert(unmber);
        InfoEntity info = new InfoEntity();
        info.setWaiguaDescribe("test");
     //   infoService.save(info);

    }

    @Override
    public UnmberEntity getByName(String waiguaUsername) {
        UnmberEntity unmberEntity = this.baseMapper.selectOne(new QueryWrapper<UnmberEntity>().eq("waigua_username", waiguaUsername));
        if(unmberEntity!=null){
            return unmberEntity;
        }else{
            return null;
        }
    }
    
    @Transactional
    @Override
    public R removeByIdWithTransaction(Long id) {
        try {
            // 执行删除操作
            boolean result = this.removeById(id);
            if (result) {
                return R.ok();
            } else {
                return R.error("删除失败，未找到指定记录");
            }
        } catch (Exception e) {
            return R.error("删除异常：" + e.getMessage());
        }
    }

    @RabbitHandler
    public void recieveMessage(Message message, InfoEntity infoEntity, Channel channel){
        System.out.println("接收到消息.："+message+"==>内容："+infoEntity);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            if(deliveryTag%2 == 0){
                //签收货物
                channel.basicAck(deliveryTag,false);
                System.out.println("签收了货物..."+deliveryTag);
            }else{
                // 退货 long var1, boolean var3=true 丢弃 boolean var4=true 发回服务器重新入库
                //拒签货物
                channel.basicNack(deliveryTag,false,true);
                System.out.println("没有签收货物》。。"+deliveryTag);
            }
        } catch (IOException e) {
            //网络中断
            e.printStackTrace();
        }
    }

}