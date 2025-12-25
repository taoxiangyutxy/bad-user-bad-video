package com.ttt.one.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttt.one.admin.entity.SysRole;
import com.ttt.one.admin.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    /**
     * 根据用户ID查询角色列表
     * 可选的动态条件：角色状态
     */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId,
                                      @Param("roleStatus") Integer roleStatus);

    /**
     * 根据条件查询用户列表
     */
    List<SysUser> selectUsersByCondition(@Param("condition") Map<String, Object> condition);

    /**
     * 统计用户数量
     */
    Long countUsersByCondition(@Param("condition") Map<String, Object> condition);

    SysUser findByUsername(@Param("username") String username);

    List<SysUser> getUsersByRoleId(@Param("roleId") Long roleId);
}
