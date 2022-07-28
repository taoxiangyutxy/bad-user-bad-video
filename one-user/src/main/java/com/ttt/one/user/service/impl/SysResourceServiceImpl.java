package com.ttt.one.user.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;

import com.ttt.one.user.dao.SysResourceDao;
import com.ttt.one.user.entity.SysResourceEntity;
import com.ttt.one.user.service.SysResourceService;


@Service("sysResourceService")
public class SysResourceServiceImpl extends ServiceImpl<SysResourceDao, SysResourceEntity> implements SysResourceService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysResourceEntity> page = this.page(
                new Query<SysResourceEntity>().getPage(params),
                new QueryWrapper<SysResourceEntity>()
        );

        return new PageUtils(page);
    }

}