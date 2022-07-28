package com.ttt.one.user.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttt.one.user.entity.SysRoleEntity;
import com.ttt.one.user.service.SysRoleService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;



/**
 * 角色表
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2022-05-02 19:24:18
 */
@RestController
@RequestMapping("order/sysrole")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("order:sysrole:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysRoleService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("order:sysrole:info")
    public R info(@PathVariable("id") Integer id){
		SysRoleEntity sysRole = sysRoleService.getById(id);

        return R.ok().put("sysRole", sysRole);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("order:sysrole:save")
    public R save(@RequestBody SysRoleEntity sysRole){
		sysRoleService.save(sysRole);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("order:sysrole:update")
    public R update(@RequestBody SysRoleEntity sysRole){
		sysRoleService.updateById(sysRole);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
  //  @RequiresPermissions("order:sysrole:delete")
    public R delete(@RequestBody Integer[] ids){
		sysRoleService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
