package com.ttt.one.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttt.one.admin.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    /**
     * 批量插入用户角色关系
     */
    int batchInsert(@Param("list") List<SysUserRole> userRoleList);

    /**
     * 根据用户ID删除角色
     */
    int deleteByUserId(@Param("userId") Long userId,
                       @Param("roleIds") List<Long> roleIds);

    void deleteByUserIds(@Param("list") List<Long> list);
}
