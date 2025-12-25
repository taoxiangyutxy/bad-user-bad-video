package com.ttt.one.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttt.one.admin.dao.SysMenuMapper;
import com.ttt.one.admin.dao.SysRoleMapper;
import com.ttt.one.admin.dao.SysUserMapper;
import com.ttt.one.admin.dao.SysUserRoleMapper;
import com.ttt.one.admin.entity.SysMenu;
import com.ttt.one.admin.entity.SysRole;
import com.ttt.one.admin.entity.SysUser;
import com.ttt.one.admin.entity.SysUserRole;
import com.ttt.one.admin.service.SysUserService;
import com.ttt.one.admin.utils.SecurityUtil;
import com.ttt.one.admin.vo.SysUserVO;
import com.ttt.one.common.exception.BizException;
import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.utils.PageAdminUtils;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("sysUserService")
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final  AuthenticationManager authenticationManager;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SecurityUtil  securityUtil;

    @Override
    public PageAdminUtils queryPageAll(Map<String, Object> params) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();

        // 1. 基础条件
        wrapper.eq("status", 1);

        // 2. 构建查询条件
        buildQueryConditions(wrapper, params);

        // 3. 添加排序（按创建时间倒序）
        wrapper.orderByDesc("create_time");

        // 4. 执行查询
        IPage<SysUser> page = this.page(
                new Query<SysUser>().getPage(params),
                wrapper
        );
        page.getRecords().forEach(user -> {
            List<SysRole> roles = sysRoleMapper.findByUserId(user.getUserId());
            user.setRoles(roles);
        });
        return new PageAdminUtils(page);
    }

    @Override
    public SysUser getUserById(Long userId) {
        return this.baseMapper.selectById(userId);
    }

    @Override
    public SysUser createUser(SysUserVO user) {
        //校验用户名是否唯一
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        if(this.baseMapper.selectOne(wrapper) != null){
            throw new BizException("用户名已存在!");
        }
        SysUser userDb = new SysUser();
        BeanUtils.copyProperties(user, userDb);
        //设置默认密码
        if(StringUtils.isEmpty(userDb.getPassword())){
            userDb.setPassword("123456");
        }
        //加密
        userDb.setPassword(new BCryptPasswordEncoder().encode(userDb.getPassword()));
        userDb.setStatus(Constant.STATUS_1);
        userDb.setCreateTime(new Date());
        //获取当前登录用户id
        Long currentUserId = securityUtil.getCurrentUserId();
        if (currentUserId != null) {
            userDb.setCreateUserId(currentUserId);
        }
        this.baseMapper.insert(userDb);

        if(!ObjectUtils.isEmpty(user.getRoleIds())){
            //添加新角色
            List<SysUserRole> userRoleList = user.getRoleIds().stream()
                    .filter(Objects::nonNull)
                    .map(roleId -> {
                        SysUserRole userRole = new SysUserRole();
                        userRole.setUserId(userDb.getUserId());
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .collect(Collectors.toList());

            if (!userRoleList.isEmpty()) {
                sysUserRoleMapper.batchInsert(userRoleList);
            }
        }
        return userDb;
    }

    @Override
    public SysUser updateUser(SysUserVO user) {
        SysUser userDb = new SysUser();
        BeanUtils.copyProperties(user, userDb);
        this.baseMapper.updateById(userDb);

        if(!ObjectUtils.isEmpty(user.getRoleIds())){
            //删除现有角色
            sysUserRoleMapper.deleteByUserId(user.getUserId(),null);
            //添加新角色
            List<SysUserRole> userRoleList = user.getRoleIds().stream()
                    .filter(Objects::nonNull)
                    .map(roleId -> {
                        SysUserRole userRole = new SysUserRole();
                        userRole.setUserId(user.getUserId());
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .collect(Collectors.toList());

            if (!userRoleList.isEmpty()) {
                sysUserRoleMapper.batchInsert(userRoleList);
            }
        }

        return userDb;
    }

    @Override
    public void deleteUser(Long [] userIds) {
        this.baseMapper.deleteBatchIds(Arrays.asList(userIds));
        sysUserRoleMapper.deleteByUserIds(Arrays.asList(userIds));

    }

    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        //删除现有角色
        sysUserRoleMapper.deleteByUserId(userId,null);
        //添加新角色
        List<SysUserRole> userRoleList = roleIds.stream()
                .filter(Objects::nonNull)
                .map(roleId -> {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    return userRole;
                })
                .collect(Collectors.toList());

        if (!userRoleList.isEmpty()) {
            sysUserRoleMapper.batchInsert(userRoleList);
        }
    }

    @Override
    public SysUser findByUsername(String username) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return this.baseMapper.selectOne(wrapper);
    }

    @Override
    public SysUser login(SysUserVO userVO) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", userVO.getUsername());
        wrapper.eq("status", 1);
        SysUser sysUser = this.baseMapper.selectOne(wrapper);
        if(sysUser != null){
            String passwordDb = sysUser.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //密码匹配
            boolean b = passwordEncoder.matches(userVO.getPassword(), passwordDb);
            if(b){
                return sysUser;
            }
        }
        return null;
    }

    @Override
    public SysUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return this.baseMapper.findByUsername(username);
    }

    @Override
    public List<String> getCurrentUserPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyList();
        }

        String username = authentication.getName();
        if (username == null) {
            return Collections.emptyList();
        }

        SysUser user = this.baseMapper.findByUsername(username);
        if (user == null) {
            return Collections.emptyList();
        }

        List<SysRole> roles = sysRoleMapper.findByUserId(user.getUserId());
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role -> sysMenuMapper.findByRoleId(role.getRoleId()))
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(SysMenu::getPerms)
                .filter(Objects::nonNull)
                .filter(perms -> !perms.trim().isEmpty())
                .map(perms -> perms.split(","))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(perm -> !perm.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<SysUser> getUsersByRoleId(Long roleId) {
        return this.baseMapper.getUsersByRoleId(roleId);
    }

    /**
     * 构建查询条件
     */
    private void buildQueryConditions(QueryWrapper<SysUser> wrapper, Map<String, Object> params) {
        // 2.1 手机号（支持模糊查询）
        if (params.containsKey("mobile") && StringUtils.isNotBlank((String) params.get("mobile"))) {
            wrapper.like("mobile", (String) params.get("mobile"));
        }

        // 2.2 邮箱（支持模糊查询）
        if (params.containsKey("email") && StringUtils.isNotBlank((String) params.get("email"))) {
            wrapper.like("email", (String) params.get("email"));
        }

        // 2.3 用户名（支持模糊查询）
        if (params.containsKey("username") && StringUtils.isNotBlank((String) params.get("username"))) {
            wrapper.like("username", (String) params.get("username"));
        }

        // 2.4 创建时间范围
        buildTimeCondition(wrapper, params, "create_time", "createTimeStart", "createTimeEnd");

        // 2.5 更新时间范围（如果需要）
        buildTimeCondition(wrapper, params, "update_time", "updateTimeStart", "updateTimeEnd");

        // 2.6 其他字段条件（可以根据需要扩展）
        if (params.containsKey("deptId") && params.get("deptId") != null) {
            wrapper.eq("dept_id", params.get("deptId"));
        }
    }

    /**
     * 构建时间范围条件
     */
    private void buildTimeCondition(QueryWrapper<SysUser> wrapper, Map<String, Object> params,
                                    String fieldName, String startParam, String endParam) {
        String startTime = (String) params.get(startParam);
        String endTime = (String) params.get(endParam);

        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            wrapper.between(fieldName, startTime, endTime);
        } else if (StringUtils.isNotBlank(startTime)) {
            wrapper.ge(fieldName, startTime);
        } else if (StringUtils.isNotBlank(endTime)) {
            wrapper.le(fieldName, endTime);
        }
    }
}
