package com.ttt.one.waiguagg.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;

import com.ttt.one.waiguagg.dao.GivelikeDao;
import com.ttt.one.waiguagg.entity.GivelikeEntity;
import com.ttt.one.waiguagg.service.GivelikeService;


@Service("givelikeService")
public class GivelikeServiceImpl extends ServiceImpl<GivelikeDao, GivelikeEntity> implements GivelikeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GivelikeEntity> page = this.page(
                new Query<GivelikeEntity>().getPage(params),
                new QueryWrapper<GivelikeEntity>()
        );

        return new PageUtils(page);
    }

}