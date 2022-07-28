package com.ttt.one.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.order.entity.OmsOrderEntity;
import com.ttt.one.order.vo.PayAsyncVo;
import com.ttt.one.order.vo.SubmitOrderResponseVo;

import java.util.Map;

/**
 * 订单
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2022-01-25 14:14:54
 */
public interface OmsOrderService extends IService<OmsOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    SubmitOrderResponseVo saveOrder(OmsOrderEntity omsOrder);

    /**
     * 支付宝异步通知后 处理支付宝的支付结果
     * @param vo
     * @return
     */
    String handlePayResult(PayAsyncVo vo);
}

