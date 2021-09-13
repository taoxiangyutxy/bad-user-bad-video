package com.ttt.one.waiguagg.controller;

import java.util.Arrays;
import java.util.Map;

import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import com.ttt.one.waiguagg.entity.UnmberEntity;
import com.ttt.one.waiguagg.service.UnmberService;

/**
 * 外挂账号
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-09 10:17:14
 */
@RestController
@RefreshScope
@RequestMapping("waiguagg/unmber")
public class UnmberController {
    @Autowired
    private UnmberService unmberService;

    @Value("${ttt.user.name}")
    private String name;
    @Value("${ttt.user.age}")
    private Integer age;

    @GetMapping("/test")
    public R test(){
        return R.ok().put("name",name).put("age",age);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = unmberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		UnmberEntity unmber = unmberService.getById(id);

        return R.ok().put("unmber", unmber);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody UnmberEntity unmber){
		unmberService.saveAndInfoAndVideo(unmber);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UnmberEntity unmber){
		unmberService.updateById(unmber);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		unmberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
