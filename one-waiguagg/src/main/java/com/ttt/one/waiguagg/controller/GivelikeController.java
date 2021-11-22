package com.ttt.one.waiguagg.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttt.one.waiguagg.entity.GivelikeEntity;
import com.ttt.one.waiguagg.service.GivelikeService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;



/**
 * 点赞表
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-11-22 16:21:39
 */
@RestController
@RequestMapping("user/givelike")
public class GivelikeController {
    @Autowired
    private GivelikeService givelikeService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("user:givelike:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = givelikeService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("user:givelike:info")
    public R info(@PathVariable("id") Long id){
		GivelikeEntity givelike = givelikeService.getById(id);

        return R.ok().put("givelike", givelike);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("user:givelike:save")
    public R save(@RequestBody GivelikeEntity givelike){
		givelikeService.save(givelike);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("user:givelike:update")
    public R update(@RequestBody GivelikeEntity givelike){
		givelikeService.updateById(givelike);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
  //  @RequiresPermissions("user:givelike:delete")
    public R delete(@RequestBody Long[] ids){
		givelikeService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
