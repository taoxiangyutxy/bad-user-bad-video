package com.ttt.one.admin.service.impl;

import com.ttt.one.admin.dao.SysMenuMapper;
import com.ttt.one.admin.dao.SysRoleMapper;
import com.ttt.one.admin.dao.SysUserMapper;
import com.ttt.one.admin.entity.SysMenu;
import com.ttt.one.admin.entity.SysRole;
import com.ttt.one.admin.entity.SysUser;

import com.ttt.one.admin.vo.UserCacheVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("======= loadUserByUsername 被调用 =======");
        log.info("加载用户: {}", username);

        // 1. 先查Redis缓存
        String cacheKey = "user:auth:" + username;
        UserCacheVO cachedUser = (UserCacheVO) redisTemplate.opsForValue().get(cacheKey);

        if (cachedUser != null) {
            log.info("✅ 从Redis缓存获取用户权限: {}", username);
            // 从数据库获取密码
            SysUser user = sysUserMapper.findByUsername(username);
            if (ObjectUtils.isEmpty(user)) {
                throw new UsernameNotFoundException("用户不存在: " + username);
            }
            if (user.getStatus() != 1) {
                throw new UsernameNotFoundException("用户已被禁用: " + username);
            }

            // 转换缓存的权限为GrantedAuthority
            List<GrantedAuthority> authorities = cachedUser.getAuthorities().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return new User(
                    user.getUsername(),
                    user.getPassword(),
                    true, true, true, true,
                    authorities
            );
        }

        // 2. 数据库加载完整信息
        SysUser user = sysUserMapper.findByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (user.getStatus() != 1) {
            log.warn("用户已被禁用: username={}, status={}", username, user.getStatus());
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 查询权限
        List<SysRole> roles = sysRoleMapper.findByUserId(user.getUserId());
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<String> authorityStrings = new ArrayList<>();

        for (SysRole role : roles) {
            String roleAuth = "ROLE_" + role.getRoleName();
            authorities.add(new SimpleGrantedAuthority(roleAuth));
            authorityStrings.add(roleAuth);

            List<SysMenu> permissions = sysMenuMapper.findByRoleId(role.getRoleId());
            for (SysMenu permission : permissions) {
                if (permission.getPerms() != null && !permission.getPerms().trim().isEmpty()) {
                    Arrays.stream(permission.getPerms().split(","))
                            .map(String::trim)
                            .filter(p -> !p.isEmpty())
                            .forEach(perm -> {
                                authorities.add(new SimpleGrantedAuthority(perm));
                                authorityStrings.add(perm);
                            });
                }
            }
        }

        log.info("用户权限列表: {}", authorityStrings);

        // 3. 缓存自定义对象
        UserCacheVO userCache = new UserCacheVO();
        userCache.setUsername(user.getUsername());
        userCache.setAuthorities(authorityStrings);
        userCache.setUserId(user.getUserId());

        redisTemplate.opsForValue().set(cacheKey, userCache, 2, TimeUnit.MINUTES);
        log.info("✅ 用户权限已缓存到Redis: {}", cacheKey);

        // 4. 返回UserDetails
        return new User(
                user.getUsername(),
                user.getPassword(),
                true, true, true, true,
                authorities
        );
    }
}
