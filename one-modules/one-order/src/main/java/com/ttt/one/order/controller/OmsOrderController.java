package com.ttt.one.order.controller;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.ttt.one.order.config.AlipayTemplate;
import com.ttt.one.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttt.one.order.entity.OmsOrderEntity;
import com.ttt.one.order.service.OmsOrderService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;

import javax.servlet.http.HttpServletRequest;

/**
 * 订单
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2022-01-25 14:14:54
 */
@RestController
@RequestMapping("/omsorder")
public class OmsOrderController {
    @Autowired
    private OmsOrderService omsOrderService;
    @Autowired
    AlipayTemplate alipayTemplate;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("order:omsorder:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = omsOrderService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 支付宝 支付成功的异步通知
     * 1.修改订单支付状态
     * @param
     * @return
     */
    @RequestMapping("/pay/alipay/success")
    public String alipay(PayAsyncVo vo,HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {
        /*Map<String, String[]> parameterMap = request.getParameterMap();
        for (String s : parameterMap.keySet()) {
            String value = request.getParameter(s);
            System.out.println("参数名："+s +"===参数值："+value);
        }
        System.out.println("数据："+parameterMap);*/

        //验签
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
         //   valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名
        if(signVerified){
            System.out.println("签名验证成功....");
            String result = omsOrderService.handlePayResult(vo);
            return result;
        }else {
            System.out.println("签名验证失败、、、");
            return "error";
        }
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("order:omsorder:info")
    public R info(@PathVariable("id") Long id){
		OmsOrderEntity omsOrder = omsOrderService.getById(id);

        return R.ok().put("omsOrder", omsOrder);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("order:omsorder:save")
    public R save(@RequestBody OmsOrderEntity omsOrder){
		omsOrderService.saveOrder(omsOrder);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("order:omsorder:update")
    public R update(@RequestBody OmsOrderEntity omsOrder){
		omsOrderService.updateById(omsOrder);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
  //  @RequiresPermissions("order:omsorder:delete")
    public R delete(@RequestBody Long[] ids){
		omsOrderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
