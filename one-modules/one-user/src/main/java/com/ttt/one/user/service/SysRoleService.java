package com.ttt.one.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.user.entity.SysRoleEntity;

import java.util.Map;

/**
 * 角色表
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2022-05-02 19:24:18
 */
public interface SysRoleService extends IService<SysRoleEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

