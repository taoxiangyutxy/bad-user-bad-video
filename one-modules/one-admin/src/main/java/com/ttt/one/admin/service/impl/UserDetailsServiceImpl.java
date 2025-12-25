package com.ttt.one.admin.service.impl;

import com.ttt.one.admin.dao.SysMenuMapper;
import com.ttt.one.admin.dao.SysRoleMapper;
import com.ttt.one.admin.dao.SysUserMapper;
import com.ttt.one.admin.entity.SysMenu;
import com.ttt.one.admin.entity.SysRole;
import com.ttt.one.admin.entity.SysUser;
import com.ttt.one.admin.service.SysMenuService;
import com.ttt.one.admin.service.SysRoleService;
import com.ttt.one.admin.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("======= loadUserByUsername 被调用 =======");
        log.info("加载用户: {}", username);
      //  log.info("调用堆栈:", new Throwable("查看调用来源"));

        // 1. 查询用户基本信息
        SysUser user = sysUserMapper.findByUsername(username);
                if(ObjectUtils.isEmpty(user)){
                    log.error("用户不存在: {}", username);
                    throw new UsernameNotFoundException("用户不存在: " + username);
                }

        log.info("找到用户: id={}, username={}, status={}",
                user.getUserId(), user.getUsername(), user.getStatus());

        // 2. 检查用户状态
        if (user.getStatus() != 1) {
            log.warn("用户已被禁用: username={}, status={}", username, user.getStatus());
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 3. 加载用户角色
        List<SysRole> roles = sysRoleMapper.findByUserId(user.getUserId());
        log.info("用户角色数量: {}，角色列表: {}",
                roles.size(),
                roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));

        // 4. 加载用户权限
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 4.1 添加角色权限
        for (SysRole role : roles) {
            // 添加角色（格式：ROLE_前缀）
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));

            // 4.2 添加角色对应的权限
            List<SysMenu> permissions = sysMenuMapper.findByRoleId(role.getRoleId());
            for (SysMenu permission : permissions) {
                if (permission.getPerms() != null && !permission.getPerms().trim().isEmpty()) {
                    String[] perms = permission.getPerms().split(",");
                    for (String perm : perms) {
                        String trimmedPerm = perm.trim();
                        if (!trimmedPerm.isEmpty()) {
                            authorities.add(new SimpleGrantedAuthority(trimmedPerm));
                        }
                    }
                }
            }
        }
        // 打印所有权限
        log.info("用户权限列表: {}",
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));

        log.info("用户加密密码：{}",user.getPassword());

        // 5. 返回UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,  // enabled
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                authorities
        );
    }
}
