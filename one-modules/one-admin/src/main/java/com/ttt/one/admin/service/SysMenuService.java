package com.ttt.one.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.admin.entity.SysMenu;
import com.ttt.one.admin.entity.SysRole;
import com.ttt.one.admin.vo.SysMenuVO;
import com.ttt.one.admin.vo.SysRoleVO;
import com.ttt.one.common.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 菜单服务接口
 *
 * @author admin
 * @since 2025-12-10
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 分页查询所有菜单
     *
     * @param params 查询参数
     * @return 分页结果
     */
    PageUtils queryPageAll(Map<String, Object> params);

    /**
     * 根据ID获取菜单信息
     *
     * @param id 菜单ID
     * @return 菜单信息
     */
    SysMenu getSysMenuById(Long id);
    
    /**
     * 创建菜单
     *
     * @param menu 菜单信息
     * @return 创建的菜单
     */
    SysMenu createSysMenu(SysMenuVO menu);
    
    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     * @return 更新后的菜单
     */
    SysMenu updateSysMenu(SysMenuVO menu);
    
    /**
     * 删除菜单
     *
     * @param ids 菜单ID数组
     */
    void deleteSysMenu(Long[] ids);

    /**
     * 根据用户id 获取菜单树形结构
     * @param userId 用户id
     * @return 菜单树形结构
     */
    List<SysMenu> selectMenusByUserId(Long userId);

    /**
     * 获取所有菜单树
     * @param condition 查询条件
     * @return 菜单树
     */
    List<SysMenu> selectMenuTree(Map<String, Object>  condition);

    /**
     * 根据角色id获取权限
     * @param roleId 角色id
     * @return  权限集合
     */
    List<SysMenu> findByRoleId(Long roleId);

    /**
     * 根据父级id获取子菜单集合
     * @param menuId 父级id
     * @return  菜单集合
     */
    List<SysMenu> findByParentId(Long menuId);

    /**
     * 获取角色名称根据关联菜单id
     * @param id 菜单id
     * @return 角色名集合
     */
    List<String> getRoleNamesByMenuId(Long id);
}