package com.ttt.one.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.order.entity.OmsOrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2022-01-25 14:14:54
 */
public interface OmsOrderOperateHistoryService extends IService<OmsOrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

