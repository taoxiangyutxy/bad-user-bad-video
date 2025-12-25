package com.ttt.one.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttt.one.admin.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * 根据用户ID查询菜单列表
     * 可选的动态条件：菜单类型、是否可见
     */
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId,
                                      @Param("menuType") String menuType,
                                      @Param("isVisible") Integer isVisible);

    /**
     * 查询根菜单
     * 可选的动态条件：菜单类型
     */
    List<SysMenu> selectRootMenus(@Param("menuType") String menuType);

    /**
     * 根据父菜单ID查询子菜单
     * 可选的动态条件：菜单类型、是否可见
     */
    List<SysMenu> selectChildrenByParentId(@Param("parentId") Long parentId,
                                           @Param("menuType") String menuType,
                                           @Param("isVisible") Integer isVisible);

    /**
     * 查询菜单树
     */
    List<SysMenu> selectMenuTree(@Param("condition") Map<String, Object> condition);

    List<SysMenu> findByRoleId(Long roleId);

    List<String> getRoleNamesByMenuId(@Param("menuId") Long menuId);
}
