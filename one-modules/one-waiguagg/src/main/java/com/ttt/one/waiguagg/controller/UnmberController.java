package com.ttt.one.waiguagg.controller;

import java.util.Arrays;
import java.util.Map;

import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import com.ttt.one.waiguagg.entity.UnmberEntity;
import com.ttt.one.waiguagg.service.UnmberService;

/**
 * 外挂账号管理控制器
 *
 * 提供外挂账号的增删改查功能
 */
@Tag(name = "外挂账号", description = "外挂账号管理")
@Slf4j
@RestController
@RefreshScope
@RequestMapping("/unmber")
@RequiredArgsConstructor
public class UnmberController {

    private final UnmberService unmberService;

    @Value("${spring.ttt.user.name}")
    private String name;
    @Value("${spring.ttt.user.age}")
    private Integer age;

    /**
     * 测试接口
     *
     * @param unmber 外挂账号实体
     * @return 测试结果
     */
    @Operation(summary = "测试接口", description = "测试外挂账号相关功能")
    @PostMapping("/test")
    public R test(@RequestBody UnmberEntity unmber) {
        log.info("unmber={}", unmber);
        return R.ok().put("name", name).put("age", age);
    }

    /**
     * 获取外挂账号列表
     *
     * @param params 查询参数
     * @return 外挂账号列表
     */
    @Operation(summary = "获取外挂账号列表", description = "分页查询外挂账号信息")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = unmberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 根据ID获取外挂账号信息
     *
     * @param id 外挂账号ID
     * @return 外挂账号信息
     */
    @Operation(summary = "获取外挂账号详情", description = "根据ID获取外挂账号详细信息")
    @Parameter(name = "id", description = "外挂账号ID", required = true)
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
		UnmberEntity unmber = unmberService.getById(id);

        return R.ok().put("unmber", unmber);
    }

    /**
     * 保存外挂账号
     *
     * @param unmber 外挂账号实体
     * @return 操作结果
     */
    @Operation(summary = "保存外挂账号", description = "创建新的外挂账号信息")
    @PostMapping("/save")
    public R save(@RequestBody UnmberEntity unmber) {
		unmberService.saveAndInfoAndVideo(unmber);

        return R.ok();
    }

    /**
     * 更新外挂账号
     *
     * @param unmber 外挂账号实体
     * @return 操作结果
     */
    @Operation(summary = "更新外挂账号", description = "修改现有外挂账号的信息")
    @PostMapping("/update")
    public R update(@RequestBody UnmberEntity unmber) {
		unmberService.updateById(unmber);

        return R.ok();
    }

    /**
     * 批量删除外挂账号
     *
     * @param ids 外挂账号ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除外挂账号", description = "根据ID数组批量删除外挂账号信息")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
		unmberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
}
