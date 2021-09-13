package com.ttt.one.waiguagg.service.impl;

import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;
import com.ttt.one.waiguagg.entity.InfoEntity;
import com.ttt.one.waiguagg.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ttt.one.waiguagg.dao.UnmberDao;
import com.ttt.one.waiguagg.entity.UnmberEntity;
import com.ttt.one.waiguagg.service.UnmberService;


@Service("unmberService")
public class UnmberServiceImpl extends ServiceImpl<UnmberDao, UnmberEntity> implements UnmberService {
    @Autowired
    private InfoService infoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UnmberEntity> page = this.page(
                new Query<UnmberEntity>().getPage(params),
                new QueryWrapper<UnmberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAndInfoAndVideo(UnmberEntity unmber) {
        this.baseMapper.insert(unmber);
        InfoEntity info = new InfoEntity();
        info.setWaiguaDescribe("test");
     //   infoService.save(info);

    }

    @Override
    public UnmberEntity getByName(String waiguaUsername) {
        UnmberEntity unmberEntity = this.baseMapper.selectOne(new QueryWrapper<UnmberEntity>().eq("waigua_username", waiguaUsername));
        if(unmberEntity!=null){
            return unmberEntity;
        }else{
            return null;
        }
    }

}