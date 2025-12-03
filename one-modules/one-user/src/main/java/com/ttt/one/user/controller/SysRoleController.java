package com.ttt.one.user.controller;

import java.util.Arrays;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.ttt.one.user.entity.SysRoleEntity;
import com.ttt.one.user.service.SysRoleService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;



/**
 * 角色管理控制器
 *
 * 提供角色的增删改查功能
 */
@Tag(name = "角色管理", description = "管理系统中的角色信息")
@RestController
@RequestMapping("/order/sysrole")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    /**
     * 获取角色列表
     *
     * @param params 查询参数
     * @return 角色列表
     */
    @Operation(summary = "获取角色列表", description = "分页查询系统中的所有角色信息")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysRoleService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 根据ID获取角色信息
     *
     * @param id 角色ID
     * @return 角色信息
     */
    @Operation(summary = "根据ID获取角色信息", description = "通过角色ID查询指定角色的详细信息")
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        SysRoleEntity sysRole = sysRoleService.getById(id);
        return R.ok().put("sysRole", sysRole);
    }

    /**
     * 保存角色
     *
     * @param sysRole 角色实体
     * @return 操作结果
     */
    @Operation(summary = "保存角色", description = "创建新的角色信息")
    @PostMapping("/save")
    public R save(@RequestBody SysRoleEntity sysRole) {
        sysRoleService.save(sysRole);
        return R.ok();
    }

    /**
     * 更新角色
     *
     * @param sysRole 角色实体
     * @return 操作结果
     */
    @Operation(summary = "更新角色", description = "修改现有角色的信息")
    @PostMapping("/update")
    public R update(@RequestBody SysRoleEntity sysRole) {
        sysRoleService.updateById(sysRole);
        return R.ok();
    }

    /**
     * 批量删除角色
     *
     * @param ids 角色ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除角色", description = "根据角色ID数组批量删除角色信息")
    @PostMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        sysRoleService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }
}
