package com.ttt.one.waiguagg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;
import com.ttt.one.waiguagg.entity.UnmberEntity;

import java.util.Map;

/**
 * 外挂账号
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-09 10:17:14
 */
public interface UnmberService extends IService<UnmberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAndInfoAndVideo(UnmberEntity unmber);

    /**
     * 根据外挂账号  查重
     * @param waiguaUsername
     * @return
     */
    UnmberEntity getByName(String waiguaUsername);
    
    /**
     * 支持分布式事务的删除方法
     * @param id
     * @return
     */
    R removeByIdWithTransaction(Long id);
}