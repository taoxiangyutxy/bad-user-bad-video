package com.ttt.one.user.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@Tag(name = "资源管理")
@RestController
@RequestMapping("order/sysresource")
@RequiredArgsConstructor
public class SysResourceController {

    private final SysResourceService sysResourceService;

    /**
     * 列表
     */
    @Operation(summary = "获取资源列表", description = "分页查询系统中的所有资源信息")
    @RequestMapping("/list")
   // @RequiresPermissions("order:sysresource:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysResourceService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @Operation(summary = "根据ID获取资源信息", description = "通过资源ID查询指定资源的详细信息")
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("order:sysresource:info")
    public R info(@PathVariable("id") Integer id){
		SysResourceEntity sysResource = sysResourceService.getById(id);

        return R.ok().put("sysResource", sysResource);
    }

    /**
     * 保存
     */
    @Operation(summary = "保存资源", description = "创建新的资源信息")
    @RequestMapping("/save")
   // @RequiresPermissions("order:sysresource:save")
    public R save(@RequestBody SysResourceEntity sysResource){
		sysResourceService.save(sysResource);

        return R.ok();
    }

    /**
     * 修改
     */
    @Operation (summary = "修改资源", description = "更新指定资源的信息")
    @RequestMapping("/update")
   // @RequiresPermissions("order:sysresource:update")
    public R update(@RequestBody SysResourceEntity sysResource){
		sysResourceService.updateById(sysResource);

        return R.ok();
    }

    /**
     * 删除
     */
    @Operation(summary = "删除资源", description = "根据资源ID删除指定资源")
    @RequestMapping("/delete")
  //  @RequiresPermissions("order:sysresource:delete")
    public R delete(@RequestBody Integer[] ids){
		sysResourceService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
