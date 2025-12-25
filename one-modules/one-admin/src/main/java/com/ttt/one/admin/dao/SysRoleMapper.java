package com.ttt.one.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttt.one.admin.entity.SysMenu;
import com.ttt.one.admin.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysMenu> listPermsByRoleId(Long roleId);

    List<SysRole> findByUserId(Long userId);
}
