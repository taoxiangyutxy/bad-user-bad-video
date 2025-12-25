package com.ttt.one.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.admin.entity.SysMenu;
import com.ttt.one.admin.entity.SysRole;
import com.ttt.one.admin.vo.SysRoleVO;
import com.ttt.one.common.utils.PageAdminUtils;

import java.util.List;
import java.util.Map;

/**
 * 角色服务接口
 *
 * @author admin
 * @since 2025-12-10
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询所有角色
     *
     * @param params 查询参数
     * @return 分页结果
     */
    PageAdminUtils queryPageAll(Map<String, Object> params);

    /**
     * 根据ID获取角色信息
     *
     * @param id 角色ID
     * @return 角色信息
     */
    SysRole getSysRoleById(Long id);
    
    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 创建的角色
     */
    SysRole createSysRole(SysRoleVO role);
    
    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 更新后的角色
     */
    SysRole updateSysRole(SysRoleVO role);
    
    /**
     * 删除角色
     *
     * @param ids 角色ID数组
     */
    void deleteSysRole(Long[] ids);

    /**
     * 角色分配权限
     * @param roleId    角色id
     * @param permIdList 权限id集合
     */
    void assignPermsToRole(Long roleId, List<Long> permIdList);

    /**
     * 获取角色的权限
     * @param roleId 角色id
     * @return 角色的权限集合
     */
    List<SysMenu> listPermsByRoleId(Long roleId);

    /**
     * 根据用户获取角色
     * @param userId 用户
     * @return 角色集合
     */
    List<SysRole> findByUserId(Long userId);
}