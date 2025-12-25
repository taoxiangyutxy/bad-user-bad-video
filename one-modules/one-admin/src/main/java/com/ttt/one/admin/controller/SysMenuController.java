package com.ttt.one.admin.controller;

import com.ttt.one.admin.entity.SysMenu;
import com.ttt.one.admin.service.SysMenuService;
import com.ttt.one.admin.utils.SecurityUtil;
import com.ttt.one.admin.vo.SysMenuVO;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "后台菜单管理")
@RequestMapping("/api/sys/menu")
@RestController
@RequiredArgsConstructor
public class SysMenuController {
    private final SysMenuService sysMenuService;
    private final SecurityUtil securityUtil;
    @Operation(summary = "菜单列表",description = "菜单列表")
    @GetMapping("/list")
    public R List(@RequestParam Map<String, Object> params){
        PageUtils page = sysMenuService.queryPageAll(params);
        return R.ok().put("page", page);
    }

    @Operation(summary = "菜单信息",description = "菜单信息")
    @GetMapping("/info/{menuId}")
    public R info(@PathVariable("menuId") Long menuId){
        if(ObjectUtils.isEmpty(menuId)){
            return R.error("菜单ID不能为空");
        }
        SysMenu menu = sysMenuService.getSysMenuById(menuId);
        return R.ok().put("data", menu);
    }

    @Operation(summary = "保存菜单",description = "保存菜单")
    @PostMapping("/save")
    public R save(@RequestBody SysMenuVO menu){
        SysMenu sysMenu = sysMenuService.createSysMenu(menu);
        return R.ok().put("data",sysMenu);
    }

    @Operation(summary = "修改菜单",description = "修改菜单")
    @PostMapping("/update")
    public R update(@RequestBody @Valid SysMenuVO menu){
        if(org.springframework.util.ObjectUtils.isEmpty(menu) || menu.getMenuId() == null){
            return R.error("菜单ID不能为空");
        }
        SysMenu sysMenu = sysMenuService.updateSysMenu(menu);
        return R.ok().put("data",sysMenu);
    }

    @Operation(summary = "删除菜单",description = "删除菜单")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] menuIds){
        if (menuIds == null || menuIds.length == 0) {
            // 处理空数组情况
            return R.error("菜单ID列表不能为空");
        }
        //遍历菜单的子菜单有没有绑定关系，如果有收集起来，最后拼接成字符串格式：不可删除，因为已经有权限关联；【菜单：工作台 已绑定：超级管理员，外挂管理员】并返回；
        // 检查要删除的菜单及其子菜单是否与角色有绑定关系
        List<String> boundMenus = checkMenuRoleBindings(menuIds);
        if (!boundMenus.isEmpty()) {
            String boundInfo = String.join("，", boundMenus);
            return R.error("不可删除，因为已经有权限关联；" + boundInfo);
        }
        sysMenuService.deleteSysMenu(menuIds);
        return R.ok();
    }

    /**
     * 检查菜单与角色的绑定关系
     * @param menuIds 要删除的菜单ID数组
     * @return 绑定信息列表，格式：【菜单：工作台 已绑定：超级管理员，外挂管理员】
     */
    private List<String> checkMenuRoleBindings(Long[] menuIds) {
        List<String> boundMenus = new ArrayList<>();

        for (Long menuId : menuIds) {
            // 获取菜单及其所有子菜单
            Set<Long> allMenuIds = getAllMenuIds(menuId);

            for (Long id : allMenuIds) {
                // 查询绑定该菜单的角色信息
                List<String> roleNames = sysMenuService.getRoleNamesByMenuId(id);
                if (roleNames != null && !roleNames.isEmpty()) {
                    SysMenu menu = sysMenuService.getSysMenuById(id);
                    String menuName = menu != null ? menu.getMenuName() : "未知菜单";
                    String roles = String.join("，", roleNames);
                    boundMenus.add("【菜单：" + menuName + " 已绑定：" + roles + "】");
                }
            }
        }

        return boundMenus;
    }

    /**
     * 递归获取菜单及其所有子菜单ID
     * @param menuId 菜单ID
     * @return 包含所有子菜单的ID集合
     */
    private Set<Long> getAllMenuIds(Long menuId) {
        Set<Long> menuIds = new HashSet<>();
        menuIds.add(menuId);

        List<SysMenu> childMenus = sysMenuService.findByParentId(menuId);
        for (SysMenu childMenu : childMenus) {
            menuIds.addAll(getAllMenuIds(childMenu.getMenuId()));
        }

        return menuIds;
    }

    @Operation(summary = "获取用户的菜单树",description = "获取用户的菜单树")
    @GetMapping("/getUserMenus")
    public R getUserMenus(@RequestParam(required = false) Long userId){
        if (userId == null) {
            Long currentUserId = securityUtil.getCurrentUserId();
            // 处理空对象或字段为空的情况
            if(currentUserId ==null){
                return R.error("用户ID不能为空");
            }
            userId = currentUserId;
        }
        List<SysMenu> menuList = sysMenuService.selectMenusByUserId(userId);
        List<SysMenu> sysMenus = buildMenuTree(menuList);
        System.out.println("menuList:"+sysMenus);
        return R.ok().put("data", buildMenuTree(menuList));
    }

    @Operation(summary = "获取菜单树",description = "获取菜单树")
    @GetMapping("/getAllMenuTree")
    public R getAllMenuTree(){
        List<SysMenu> menuList = sysMenuService.selectMenuTree(new HashMap<>());
        return R.ok().put("data", buildMenuTree(menuList));
    }

    /**
     * 构建菜单树
     * @param menus 完整菜单列表
     * @return 根菜单节点列表，已构建完整的树形结构
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        // 筛选出所有根菜单（parentId为0的菜单）
        List<SysMenu> rootMenus = menus.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0)
                .sorted(Comparator.comparing(SysMenu::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

        // 为每个根菜单递归构建子菜单树
        for (SysMenu rootMenu : rootMenus) {
            buildChildren(rootMenu, menus);
        }

        return rootMenus;
    }

    /**
     * 递归构建子菜单
     * @param parentMenu 父菜单节点
     * @param allMenus 所有菜单列表
     */
    private void buildChildren(SysMenu parentMenu, List<SysMenu> allMenus) {
        // 查找当前父菜单的所有直接子菜单
        List<SysMenu> children = allMenus.stream()
                .filter(menu -> menu.getParentId() != null &&
                        menu.getParentId().equals(parentMenu.getMenuId()))
                .sorted(Comparator.comparing(SysMenu::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

        // 如果存在子菜单，则设置并递归构建孙子菜单
        if (!children.isEmpty()) {
            parentMenu.setChildren(children);
            for (SysMenu child : children) {
                buildChildren(child, allMenus);
            }
        }
    }

}
