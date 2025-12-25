package com.ttt.one.admin.controller;

import com.ttt.one.admin.entity.SysUser;
import com.ttt.one.admin.service.SysUserService;
import com.ttt.one.admin.vo.SysUserVO;
import com.ttt.one.admin.vo.UserRoleVO;
import com.ttt.one.common.utils.PageAdminUtils;
import com.ttt.one.common.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "后台用户管理")
@RequestMapping("/api/sys/user")
@RestController
@RequiredArgsConstructor
public class SysUserController {
    private final SysUserService sysUserService;
    @PreAuthorize("hasAuthority('sys:user:list')")
    @Operation(summary = "用户列表",description = "用户列表")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageAdminUtils page = sysUserService.queryPageAll(params);
        return R.ok().put("data", page);
    }
    @Operation(summary = "用户信息",description = "用户信息")
    @GetMapping ("/info/{userId}")
        public R info(@PathVariable("userId") Long userId) {
        if(ObjectUtils.isEmpty(userId)){
            return R.error("用户ID不能为空");
        }
        SysUser user = sysUserService.getUserById(userId);
        return R.ok().put("data", user);
    }

    @Operation(summary = "保存用户",description = "保存用户")
    @PostMapping("/save")
    public R save(@RequestBody SysUserVO user) {
        SysUser sysUser = sysUserService.createUser(user);
        return R.ok().put("data",sysUser);
    }
    @Operation(summary = "修改用户",description = "修改用户")
    @PostMapping("/update")
    public R update(@RequestBody  SysUserVO user) {
        if(ObjectUtils.isEmpty(user) || user.getUserId() == null){
            return R.error("用户ID不能为空");
        }
        SysUser sysUser = sysUserService.updateUser(user);
        return R.ok().put("data",sysUser);
    }
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @Operation(summary = "删除用户",description = "删除用户")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            // 处理空数组情况
            return R.error("用户ID列表不能为空");
        }
        sysUserService.deleteUser(ids);
        return R.ok();
    }
    @Operation(summary = "分配角色",description = "分配角色")
    @PostMapping("/assignRoles")
    public R assignRoles(@RequestBody @Valid UserRoleVO vo) {
        if (vo == null || vo.getUserId() == null || vo.getRoleIds() == null) {
            // 处理空对象或字段为空的情况
            return R.error("用户ID或角色ID列表不能为空");
        }
        Long userId = vo.getUserId();
        String roleIdsStr = vo.getRoleIds();
        // 将字符串转换为 List<Long>
        List<Long> roleIdList = Arrays.stream(roleIdsStr.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        sysUserService.assignRolesToUser(userId, roleIdList);
        return R.ok();
    }
}
