package com.ttt.one.order.web;

import com.ttt.one.order.entity.OmsOrderEntity;
import com.ttt.one.order.service.OmsOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    private OmsOrderService omsOrderService;
    /**
     * 默认页
     * @param params
     * @param model
     * @return
     */
    @GetMapping({"/","/index.html"})
    public String indexPage(@RequestParam Map<String, Object> params, Model model){
        //有默认前缀  和默认后缀 所以直接index即可   视图解析器进行拼串
        return "cartList";
    }

    /**
     * 代付款页面
     * @param params
     * @param model
     * @return
     */
    @GetMapping({"/daiFuKuan.html"})
    public String daiFuKuanPage(@RequestParam Map<String, Object> params, Model model){
        //有默认前缀  和默认后缀 所以直接index即可   视图解析器进行拼串
        return "indexDaiFuKuan";
    }

    /**
     * 结算页
     * @param params
     * @param model
     * @return
     */
    @GetMapping({"/jieSuan.html"})
    public String jieSuanPage(@RequestParam Map<String, Object> params, Model model){
        //有默认前缀  和默认后缀 所以直接index即可   视图解析器进行拼串
        return "indexJieSuan";
    }

    /**
     * 订单页
     * @param params
     * @param model
     * @return
     */
    @GetMapping({"/order.html"})
    public String orderPage(@RequestParam Map<String, Object> params, Model model){
        //有默认前缀  和默认后缀 所以直接index即可   视图解析器进行拼串
        return "indexOrder";
    }

    /**
     * 收银页
     * @param params
     * @param model
     * @return
     */
    @GetMapping({"/shouYin.html"})
    public String shouYinPage(@RequestParam Map<String, Object> params, Model model){
        //有默认前缀  和默认后缀 所以直接index即可   视图解析器进行拼串
        return "indexShouYin";
    }

    /**
     * 生成订单后去支付
     * @param model
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(Model model, RedirectAttributes redirectAttributes){
        OmsOrderEntity order = new OmsOrderEntity();
        try{
            int number = (int)(Math.random()*10000)+1;
            order.setOrderSn(number+"");
        //    omsOrderService.saveOrder(order);
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("msg","下单失败");
            return "redirect:http://order.waiguattt.com/jieSuan.html";
        }
        //下单：去创建订单，验令牌，验价格，锁库存。。。
        //下单成功来到支付选择页
        //下单失败回到订单确认页重新确认订单信息
        System.out.println("订单提交的数据");
        if (true){
            //下单成功来到支付选择页
            model.addAttribute("submitOrderResp", order);
            return "indexShouYin";
        }else {
            String msg = "下单失败;";
            switch (1){
                case 1: msg+="订单信息过期，请刷新再提交"; break;
                case 2: msg+="订单商品价格发生变化，请确认后再次提交"; break;
                case 3: msg+="库存锁定失败，商品库存不足"; break;
            }
            redirectAttributes.addFlashAttribute("msg",msg);
            return "redirect:http://order.waiguattt.com/jieSuan.html";
        }

    }
}
