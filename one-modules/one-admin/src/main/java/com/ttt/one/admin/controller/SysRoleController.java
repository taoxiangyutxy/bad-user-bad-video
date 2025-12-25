package com.ttt.one.admin.controller;

import com.ttt.one.admin.entity.SysMenu;
import com.ttt.one.admin.entity.SysRole;
import com.ttt.one.admin.entity.SysUser;
import com.ttt.one.admin.service.SysRoleService;
import com.ttt.one.admin.service.SysUserService;
import com.ttt.one.admin.vo.RoleMenuVO;
import com.ttt.one.admin.vo.SysRoleVO;
import com.ttt.one.common.utils.PageAdminUtils;
import com.ttt.one.common.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "后台角色管理")
@RequestMapping("/api/sys/role")
@RestController
@RequiredArgsConstructor
public class SysRoleController {
    private final SysRoleService sysRoleService;
    private final SysUserService sysUserService;
    @PreAuthorize("hasAuthority('sys:role:list')")
    @Operation(summary = "角色列表",description = "角色列表")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageAdminUtils page = sysRoleService.queryPageAll(params);
        return R.ok().put("data", page);
    }

    @Operation(summary = "角色信息",description = "角色信息")
    @GetMapping("/info/{roleId}")
    public R info(@PathVariable("roleId") Long roleId) {
        if(ObjectUtils.isEmpty(roleId)){
            return R.error("角色ID不能为空");
        }
        SysRole role = sysRoleService.getSysRoleById(roleId);
        return R.ok().put("data", role);
    }

    @Operation(summary = "保存角色",description = "保存角色")
    @PostMapping("/save")
    public R save(@RequestBody SysRoleVO role) {
        SysRole sysRole = sysRoleService.createSysRole(role);
        return R.ok().put("data",sysRole);
    }
    @Operation(summary = "修改角色",description = "修改角色")
    @PostMapping("/update")
    public R update(@RequestBody @Valid SysRoleVO role) {
        if(org.springframework.util.ObjectUtils.isEmpty(role) || role.getRoleId() == null){
            return R.error("角色ID不能为空");
        }
        SysRole sysRole = sysRoleService.updateSysRole(role);
        return R.ok().put("data",sysRole);
    }
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @Operation(summary = "删除角色",description = "删除角色")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] roleIds) {
        if (roleIds == null || roleIds.length == 0) {
            // 处理空数组情况
            return R.error("角色ID列表不能为空");
        }
        for (Long roleId : roleIds) {
        List<SysUser>  users =   sysUserService.getUsersByRoleId(roleId);
        if (!users.isEmpty()) {
            return R.error("该角色下有用户，请先解除用户:[" + users.stream().map(SysUser::getUsername).collect(Collectors.joining(",")) + "]的相关权限！");
            }
        }
        
        sysRoleService.deleteSysRole(roleIds);
        return R.ok();
    }

    @Operation(summary = "角色分配权限",description = "角色分配权限")
    @PostMapping("/assignPermsToRole")
    public R assignPermsToRole(@RequestBody @Valid RoleMenuVO vo) {
        if (vo == null || vo.getRoleId() == null || vo.getMenuIds() == null) {
            // 处理空对象或字段为空的情况
            return R.error("角色ID或权限ID列表不能为空");
        }
        Long roleId = vo.getRoleId();
        String permissionIdsStr = vo.getMenuIds();
        // 将字符串转换为 List<Long>
        List<Long> permIdList = Arrays.stream(permissionIdsStr.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        sysRoleService.assignPermsToRole(roleId, permIdList);
        return R.ok();
    }

    @Operation(summary = "角色权限列表",description = "角色权限列表")
    @GetMapping("/listPermsByRoleId")
    public R listPermsByRoleId(@RequestParam("roleId") Long roleId) {
        if (roleId == null) {
            // 处理空对象或字段为空的情况
            return R.error("角色ID不能为空");
        }
        List<SysMenu> permList = sysRoleService.listPermsByRoleId(roleId);
        return R.ok().put("data", permList);
    }

}
