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

import com.ttt.one.user.entity.SysResourceEntity;
import com.ttt.one.user.service.SysResourceService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;



/**
 * 资源表
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2022-05-02 19:24:18
 */
@RestController
@RequestMapping("order/sysresource")
public class SysResourceController {
    @Autowired
    private SysResourceService sysResourceService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("order:sysresource:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysResourceService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("order:sysresource:info")
    public R info(@PathVariable("id") Integer id){
		SysResourceEntity sysResource = sysResourceService.getById(id);

        return R.ok().put("sysResource", sysResource);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("order:sysresource:save")
    public R save(@RequestBody SysResourceEntity sysResource){
		sysResourceService.save(sysResource);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("order:sysresource:update")
    public R update(@RequestBody SysResourceEntity sysResource){
		sysResourceService.updateById(sysResource);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
  //  @RequiresPermissions("order:sysresource:delete")
    public R delete(@RequestBody Integer[] ids){
		sysResourceService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
