package com.ttt.one.order.vo;
import com.ttt.one.order.entity.OmsOrderEntity;
import lombok.Data;

/**
 * @author liuzemin
 * @date 2021/3/9 10:16
 * @description
 */
@Data
public class SubmitOrderResponseVo {
    private OmsOrderEntity order;

    private Integer code; //0成功   错误状态码
}
