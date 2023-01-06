package com.ttt.one.order.service.impl;

import com.ttt.one.order.vo.PayAsyncVo;
import com.ttt.one.order.vo.SubmitOrderResponseVo;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;

import com.ttt.one.order.dao.OmsOrderDao;
import com.ttt.one.order.entity.OmsOrderEntity;
import com.ttt.one.order.service.OmsOrderService;
import org.springframework.transaction.annotation.Transactional;

@Service("omsOrderService")
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderDao, OmsOrderEntity> implements OmsOrderService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OmsOrderEntity> page = this.page(
                new Query<OmsOrderEntity>().getPage(params),
                new QueryWrapper<OmsOrderEntity>()
        );

        return new PageUtils(page);
    }

    //本地事务，在分布式系统，只能控制住自己的回滚，控制不了其他服务的回滚
    //分布式事务：最大原因。网络问题+分布式机器。
   // @GlobalTransactional  //高并发
    @Transactional
    @Override
    public SubmitOrderResponseVo saveOrder(OmsOrderEntity vo) {
       // confirmVoThreadLocal.set(vo);  公共线程缓存里放
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        response.setCode(0);
        //1、验证令牌[令牌的对比和删除必须保证原子性]
        // 0令牌失败 -  1删除成功
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        String orderToken = "vo.getOrderToken()";
        //原子验证令牌和删除令牌
        Long result = 1L; //redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if (result == 0L){
            //令牌验证失败
            return response;
        }else {
            //令牌验证成功
            //下单：去创建订单，验令牌，验价格，锁库存。。。
            //1、创建订单，订单项等信息
           // OrderCreateTo order = createOrder();
            //2、验价
           // BigDecimal payAmount = order.getOrder().getPayAmount();
          //  BigDecimal payPrice = vo.getPayPrice();
            if (true){  //Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01
                //金额对比
                //。。。
                //TODO 3、保存订单
                this.save(vo);
                //4、库存锁定.只要有异常回滚订单数据。
                //订单号，所有订单项（skuId，skuName，num）
//                WareSkuLockVo lockVo = new WareSkuLockVo();
//                lockVo.setOrderSn(order.getOrder().getOrderSn());
//                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
//                    OrderItemVo itemVo = new OrderItemVo();
//                    itemVo.setSkuId(item.getSkuId());
//                    itemVo.setCount(item.getSkuQuantity());
//                    itemVo.setTitle(item.getSkuName());
//                    return itemVo;
//                }).collect(Collectors.toList());
//                lockVo.setLocks(locks);
                //TODO 4、远程锁库存
                //库存成功了，但是网络原因超时了，订单回滚，库存不滚

                //为了保证高并发。库存服务自己回滚。可以发消息给库存服务；
                //库存服务本身也可以使用自动解锁模式  消息队列
               // R r = wmsFeignService.orderLockStock(lockVo);
                /*if (r.getCode() == 0){
                    //锁成功了
                    response.setOrder(order.getOrder());
                    //TODO 5、远程扣减积分 出异常
//                    int i = 10/0; //订单回滚，库存不滚
                    //TODO 订单创建成功发送消息给MQ
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrder());
                    return response;
                }else {
                    //锁定失败
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                }*/

            }else {
                response.setCode(2);
                return response;
            }

        }
//        String redisToken = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId());
//        if (orderToken!=null && orderToken.equals(redisToken)){
//            //令牌验证通过
//            redisTemplate.delete(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId());
//        }else {
//            //不通过
//
//        }
        response.setCode(0);
        return response;
    }

    @Override
    public String handlePayResult(PayAsyncVo vo) {
        //1.保存交易流水 到流水表
        //2.判断支付宝支付状态。修改订单的状态信息.
        System.out.println("订单状态已修改!");
        return "success";
    }

}