package com.ttt.one.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttt.one.admin.dao.SysRoleMapper;
import com.ttt.one.admin.dao.SysRoleMenuMapper;
import com.ttt.one.admin.entity.SysMenu;
import com.ttt.one.admin.entity.SysRole;
import com.ttt.one.admin.entity.SysRoleMenu;
import com.ttt.one.admin.service.SysRoleService;
import com.ttt.one.admin.utils.SecurityUtil;
import com.ttt.one.admin.vo.SysRoleVO;
import com.ttt.one.common.utils.PageAdminUtils;
import com.ttt.one.common.utils.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Autowired
    private SecurityUtil securityUtil;
    @Override
    public PageAdminUtils queryPageAll(Map<String, Object> params) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();

        wrapper.orderByDesc("create_time");

        if (params.containsKey("roleName") && StringUtils.isNotBlank((String) params.get("roleName"))) {
            wrapper.like("role_name", (String) params.get("roleName"));
        }

        IPage<SysRole> page = this.page(
                new Query<SysRole>().getPage(params),
                wrapper
        );

        return new PageAdminUtils(page);
    }

    @Override
    public SysRole getSysRoleById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public SysRole createSysRole(SysRoleVO role) {
        SysRole roleDb = new SysRole();
        BeanUtils.copyProperties(role, roleDb);
        roleDb.setCreateTime(new Date());
        roleDb.setCreateUserId(securityUtil.getCurrentUserId());
        this.baseMapper.insert(roleDb);
        return roleDb;
    }

    @Override
    public SysRole updateSysRole(SysRoleVO role) {
        SysRole roleDb = new SysRole();
        BeanUtils.copyProperties(role, roleDb);
        this.baseMapper.updateById(roleDb);
        return roleDb;
    }

    @Override
    public void deleteSysRole(Long[] ids) {
        //todo 如果有用户已经有了该权限  不能随意删除已经有关联的数据
        this.baseMapper.deleteBatchIds(Arrays.asList(ids));
        for (Long id : ids) {
            sysRoleMenuMapper.deleteByRoleId(id,null);
        }
    }

    @Override
    public void assignPermsToRole(Long roleId, List<Long> permIdList) {
        //删除现有权限
        sysRoleMenuMapper.deleteByRoleId(roleId,null);
        //添加新权限
        List<SysRoleMenu> rolePermissions = permIdList.stream()
                .filter(Objects::nonNull)
                .map(menuId -> {
                    SysRoleMenu rolePermission = new SysRoleMenu();
                    rolePermission.setRoleId(roleId);
                    rolePermission.setMenuId(menuId);
                    return rolePermission;
                })
                .collect(Collectors.toList());

        if (!rolePermissions.isEmpty()) {
            sysRoleMenuMapper.batchInsert(rolePermissions);
        }
    }

    @Override
    public List<SysMenu> listPermsByRoleId(Long roleId) {
        return  this.baseMapper.listPermsByRoleId(roleId);
    }

    @Override
    public List<SysRole> findByUserId(Long userId) {
        return this.baseMapper.findByUserId(userId);
    }
}
