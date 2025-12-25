package com.ttt.one.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttt.one.admin.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 批量插入角色权限关系
     */
    int batchInsert(@Param("list") List<SysRoleMenu> roleMenuList);

    /**
     * 根据角色ID删除权限
     */
    int deleteByRoleId(@Param("roleId") Long roleId,
                       @Param("menuIds") List<Long> menuIds);
}
