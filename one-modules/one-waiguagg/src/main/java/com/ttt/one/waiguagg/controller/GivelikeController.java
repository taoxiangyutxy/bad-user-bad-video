package com.ttt.one.waiguagg.controller;

import java.util.Arrays;
import java.util.Map;

import com.ttt.one.oplog.annotation.OperationLog;
import com.ttt.one.oplog.annotation.OperationLogType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.ttt.one.waiguagg.entity.GivelikeEntity;
import com.ttt.one.waiguagg.service.GivelikeService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;



/**
 * 点赞管理控制器
 *
 * 提供点赞信息的增删改查功能
 */
@Tag(name = "点赞管理", description = "点赞信息管理")
@RestController
@RequestMapping("/givelike")
@RequiredArgsConstructor
public class GivelikeController {

    private final GivelikeService givelikeService;

    /**
     * 获取点赞列表
     *
     * @param params 查询参数
     * @return 点赞列表
     */
    @Operation(summary = "获取点赞列表", description = "分页查询点赞信息")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = givelikeService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 根据ID获取点赞信息
     *
     * @param id 点赞ID
     * @return 点赞信息
     */
    @Operation(summary = "根据ID获取点赞信息", description = "通过点赞ID查询指定点赞的详细信息")
    @Parameter(name = "id", description = "点赞ID")
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
		GivelikeEntity givelike = givelikeService.getById(id);

        return R.ok().put("givelike", givelike);
    }

    /**
     * 保存点赞
     *
     * @param givelike 点赞实体
     * @return 操作结果
     */
    @OperationLog(desc = "保存点赞", type= OperationLogType.ADD)
    @Operation(summary = "保存点赞", description = "创建新的点赞信息")
    @PostMapping("/save")
    public R save(@RequestBody GivelikeEntity givelike) {
		givelikeService.save(givelike);

        return R.ok();
    }

    /**
     * 更新点赞
     *
     * @param givelike 点赞实体
     * @return 操作结果
     */
    @OperationLog(desc = "更新点赞", type= OperationLogType.UPDATE)
    @Operation(summary = "更新点赞", description = "修改现有点赞的信息")
    @PostMapping("/update")
    public R update(@RequestBody GivelikeEntity givelike) {
		givelikeService.updateById(givelike);

        return R.ok();
    }

    /**
     * 批量删除点赞
     *
     * @param ids 点赞ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除点赞", description = "根据点赞ID数组批量删除点赞信息")
    @Parameter(name = "ids", description = "点赞ID数组")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
		givelikeService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
