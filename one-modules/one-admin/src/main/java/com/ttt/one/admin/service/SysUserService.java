package com.ttt.one.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.admin.entity.SysUser;
import com.ttt.one.admin.vo.SysUserVO;
import com.ttt.one.common.utils.PageAdminUtils;
import com.ttt.one.common.utils.PageUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 获取所有用户列表
     * 
     * @return 用户列表
     */
    PageAdminUtils queryPageAll(Map<String, Object> params);
    /**
     * 根据用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser getUserById(Long userId);
    
    /**
     * 创建用户
     * 
     * @param user 用户信息
     * @return 创建后的用户信息
     */
    SysUser createUser(SysUserVO user);
    
    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    SysUser updateUser(SysUserVO user);
    
    /**
     * 删除用户
     * 
     * @param userIds 用户ID
     */
    void deleteUser(Long[] userIds);
    
    /**
     * 为用户分配角色
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 用户名获取用户
     * @param username 用户名
     * @return 用户
     */
    SysUser findByUsername(String username);

    /**
     * 登录
     * @param userVO 用户信息
     * @return 用户
     */
    SysUser login(SysUserVO userVO);

    /**
     * 获取当前登录用户
     * @return 用户
     */
    SysUser getCurrentUser();

    /**
     * 获取当前登录用户权限
     * @return 权限列表
     */
    List<String> getCurrentUserPermissions();


    List<SysUser> getUsersByRoleId(Long roleId);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户实体
     */
    SysUser getUserByUsername(String username);

    /**
     * 重置用户密码
     *
     * @param username 用户名
     * @param newPassword 新密码
     */
    void resetPassword(String username, String newPassword);
}
