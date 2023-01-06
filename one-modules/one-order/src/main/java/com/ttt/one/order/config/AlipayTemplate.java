package com.ttt.one.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.ttt.one.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/*绑定配置文件里的 alipay*/
@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000119611270";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key ="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCM9DwSYmS42BZqbG83jCLcRtOqjC9p/osPu7PDW8qykaMpY9DjoSdOl4BHcLPWD3WyOarcAzaHqEl/fGgJ3/LE4i+48b32MHSooeVrIgD1PLJNC4WhXMxCuLBXwIV/LKKbhRh7bKvzdDuG3nT0I5DIoddp8CczE5+IqFAU8KVP4ug2jPkhu10Rs23H7NmdznAXyV/2oh1QIY5Oi9braVUsStpXkuBOOq2MA9mxGkH940gwL/Dunc02fv2S64IKaxtNWTjuj/neeXyQO6Kla68aKzcv6N1zzNszDDFidtrINRriSX1EQYimAI4rWtcY7pb+GxgB9LrLJx40ml9RtcJ1AgMBAAECggEAI3E6Nak2CGCValcvs/jxvw+0I3syZOWTU/ZvohzPoA0MyNiHauVXu+ROZihJn/atMo5bjwzjxkKjkIHYqzr7kVQKieeHS4MtokVrKYAYW5HSh5rxE/0Ag3prJPWM28h+brqNyGMv7Xx5h1UidU0IF9rSDppLUQWLJNKlqE5GUwqsJh7SGvEsM47Ls41Hif53Wi6tamBaNAu7x19nTJnHHRPjPybRiX6UWlPgA6oB7C8LzH1oQ44zc6R3oYLctm4qbPiNXG9N+y9cYqpQxczHdaBR1kKBXBFn3XVStlsg47SJiAcvuwo0A1zBdTGWHInkxbm1l6N84U981Xy0rkswtQKBgQDTL4YUjf/xiCVHjXIrqaYDucCEwrsp914+AgC44GZsdfT36IELCFdJiIXEdvYZRdHHfJMqf3+UUOVx9vVDDE+6Si3nhbYFJd2Jkgak9SQ2/VNE4sfZoJ0UI9gVfzLQboaza0r2/pNe+3JVtiEJxrQeBZg4ZyHfrAR3CCteiIXTowKBgQCq3XC+jSAdfnqaNO788c/iaDmccNlpIFdRTp9NZLV00u9tOOX7m7t/1o/ddsIX8DZKKmuYa5Ghjt6b/7gyM+v78KbkplgD8BBEDccTGAs8e7v5927Zz0KLeDOAG8jIVFid2/28aGrdWZNZkEl03QhqCDWk+HeImm4qgZZxkcazBwKBgQCPDtNqGyM1D8AJL3UWxM5UGcGjbXJyJELwCFVK56AaevFd+l9oBNd0VYogI2HkjtpjoyziDgpzgzVatStBpxZfIJ7mBjreU981sPjVR8anhYWh1WoCL34YxYs8TLU0YprW8aiPlrZoEjdKfpz0zWj4KVwLYTmeFh3UMHGIkTzutwKBgCRH+RS9KsCYhrgP3V9giV/t+a26WK/16hSduT3Z0J4J3EU4+7y9iMbQbKP9/6DNpPmCoG4xCS9hAuvlFNu0IrvQkd+jYrUTWKDavBbV2CzzqSqe267sT5kF4Qmm3K+NGXQLrok/MtHln2A5Px/Nf2fkbl+lUtxyzknAUo2zApopAoGBALaQ4Yxx47Ay2HcGHQIcBtKyPZpbhz2KELcPyxABxi0bJHTfI9Hp3EWrPX4FoO/AQuQCjICbKVuCU04UsdlmE7YufdA3n+W0JhPyZOjSWsuQ21UQmPXrMj0PieABvKMm/ur9uJTPCQOSiCpXg/mGMRc4u3lQIpGFQVInkPfqQuRY";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq63LKo0T9g82iHgGwmiWD3URcWsx55oJnrt8Y0jjE5mCuO4PImNAWgM7Oh0oIaPncj91no8t2JvbyS2Ewkmhs8MdPPVBPBPAEXo1Zm+7OS19D8TzRlU+OPUSBNhtdpFoJNSfy/b/ij7vRxTI8zutAjtPsdJHcfMgEysvrzo4bxtbrz6E33dSusXSP+ZLPBB/vWYzMrhZe3UPto95ycxZOI7pq6c0S29J8JpSmG5cSa2KK5nnNL5NnAgK1+CkYRUr6OrP8afUVQesMZb8uUWrgTkNlcfbK8eoKMm2W4wORvUq1aGwkTGqA/UH7l1DmkMUgj74k0vj0yfhF6ThyMdB9wIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\"1m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
