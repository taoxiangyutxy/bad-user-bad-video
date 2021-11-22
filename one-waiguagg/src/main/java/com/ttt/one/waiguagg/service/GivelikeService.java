package com.ttt.one.waiguagg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.waiguagg.entity.GivelikeEntity;

import java.util.Map;

/**
 * 点赞表
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-11-22 16:21:39
 */
public interface GivelikeService extends IService<GivelikeEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

