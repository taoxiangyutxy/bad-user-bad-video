package com.ttt.one.order.web;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttt.one.order.config.AlipayTemplate;
import com.ttt.one.order.entity.OmsOrderEntity;
import com.ttt.one.order.service.OmsOrderService;
import com.ttt.one.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author liuzemin
 * @date 2021/3/16 13:28
 * @description
 */
@Controller
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OmsOrderService orderService;


    /**
     * 1、将支付页让浏览器展示
     * 2、支付成功以后，我们要跳到用户的订单列表页
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

//        PayVo payVo = new PayVo();
//        payVo.setBody();//订单的备注
//        payVo.setOut_trade_no();//订单号
//        payVo.setSubject();//订单的主题
//        payVo.setTotal_amount();
        OmsOrderEntity order_sn = orderService.getOne(new QueryWrapper<OmsOrderEntity>().eq("order_sn", orderSn));
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderSn);
        payVo.setSubject("测试");
        payVo.setTotal_amount("11");
        //返回的是一个页面。将此页面直接交给浏览器就行
        String pay = alipayTemplate.pay(payVo);
        System.out.println(pay);
        return pay;
    }
}
























