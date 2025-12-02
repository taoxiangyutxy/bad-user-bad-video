package com.ttt.one.waiguagg.controller;

import java.util.*;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;
import com.ttt.one.waiguagg.entity.GivelikeEntity;
import com.ttt.one.waiguagg.vo.VideoPreviewVO;
import com.ttt.one.waiguagg.vo.WaiGuaInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ttt.one.waiguagg.entity.InfoEntity;
import com.ttt.one.waiguagg.service.InfoService;

/**
 * 一个外挂账号,会有多个举报信息,直到被永封该账号不会再接受新的举报信息。
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-09 10:17:14
 */
@Tag(name = "外挂举报信息管理", description = "外挂账号举报信息相关接口")
@RestController
@RequestMapping("waiguagg/info")
public class InfoController {
    @Autowired
    private InfoService infoService;

    /**
     * 返回视频预览 信息列表  根据外挂信息id
     */
    @Operation(summary = "获取视频预览列表", description = "根据外挂信息ID获取视频预览列表")
    @RequestMapping("/videoList/{id}")
    public R videoList(@Parameter(description = "外挂信息ID") @PathVariable("id") Long id){
        List<VideoPreviewVO> videolist = infoService.videolistByInfoId(id);
        return R.ok().put("videolist", videolist);
    }


    /**
     * 更新审核状态
     * @param waiGuaInfoVO
     * @return
     */
    @Operation(summary = "更新审核状态", description = "更新外挂举报的审核状态")
    @RequestMapping("/updateReview")
    public R updateReview(@RequestBody WaiGuaInfoVO waiGuaInfoVO){
        infoService.updateByIdAndReview(waiGuaInfoVO);

        return R.ok();
    }

    /**
     * 查出所有待审核 数据列表
     * @param params
     * @param reviewVal
     * @return
     */
    @Operation(summary = "查询审核列表", description = "根据审核状态查询待审核数据列表")
    @GetMapping("/listReview/{reviewVal}")
    public R listReview(@RequestParam Map<String, Object> params,
                        @Parameter(description = "审核状态值") @PathVariable("reviewVal")Long reviewVal){
        PageUtils page = infoService.queryPageAllByReview(params,reviewVal);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @Operation(summary = "获取列表", description = "获取所有外挂举报信息列表")
    @RequestMapping("/list")
    //@RequiresPermissions("waiguagg:info:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = infoService.queryPageAll(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @Operation(summary = "获取详细信息", description = "根据ID获取外挂举报详细信息")
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("waiguagg:info:info")
    public R info(@Parameter(description = "举报信息ID") @PathVariable("id") Long id){
        WaiGuaInfoVO infoVO = infoService.getByIdAndUnmber(id,-1L);
        return R.ok().put("info", infoVO);
    }

    /**
     * 保存   管理员管理平台
     */
    @Operation(summary = "保存举报信息", description = "管理员平台保存外挂举报信息")
    @RequestMapping("/save")
  //  @RequiresPermissions("waiguagg:info:save")
    public R save(@RequestBody WaiGuaInfoVO waiGuaInfoVO){
		infoService.saveUnmberAndInfo(waiGuaInfoVO);

        return R.ok();
    }
    /**
     * 保存   前台
     */
    @Operation(summary = "前台保存并更新文件", description = "前台用户提交举报信息并上传文件")
    @RequestMapping("/saveAndUpdateFile")
    public R saveAndUpdateFile(@RequestBody WaiGuaInfoVO waiGuaInfoVO){
        infoService.saveAndUpdateFile(waiGuaInfoVO);
        return R.ok();
    }
    /**
     * 修改
     */
    @Operation(summary = "修改举报信息", description = "修改外挂举报信息")
    @RequestMapping("/update")
   // @RequiresPermissions("waiguagg:info:update")
    public R update(@RequestBody WaiGuaInfoVO waiGuaInfoVO){
		infoService.updateByIdAndUnmber(waiGuaInfoVO);

        return R.ok();
    }

    /**
     * 删除
     */
    @Operation(summary = "批量删除", description = "批量删除外挂举报信息")
    @RequestMapping("/delete")
   // @RequiresPermissions("waiguagg:info:delete")
    public R delete(@Parameter(description = "要删除的ID数组") @RequestBody Long[] ids){
		infoService.removeByIdsAllIn(Arrays.asList(ids));

        return R.ok();
    }
    /**
     *  描述: 点赞
     * @param givelikeEntity:
     * @return R
     * @author txy
     * @description
     * @date 2021/11/22 16:52
     */
    @Operation(summary = "点赞", description = "对外挂举报信息进行点赞")
    @RequestMapping(value = "/giveLikeInfo",method = RequestMethod.POST)
    public R giveLikeInfo(@RequestBody GivelikeEntity givelikeEntity){
        infoService.giveLikeInfo(givelikeEntity.getRelationId(),givelikeEntity.getUserId(),givelikeEntity.getType());
        return R.ok("点赞成功");
    }
    /**
     *  描述: 取消点赞
     * @param givelikeEntity:
     * @return R
     * @author txy
     * @description
     * @date 2021/11/22 16:52
     */
    @Operation(summary = "取消点赞", description = "取消对外挂举报信息的点赞")
    @RequestMapping(value = "/unGiveLikeInfo",method = RequestMethod.POST)
    public R unGiveLikeInfo(@RequestBody GivelikeEntity givelikeEntity){
        infoService.unGiveLikeInfo(givelikeEntity.getRelationId(),givelikeEntity.getUserId(),givelikeEntity.getType());
        return R.ok("取消点赞成功");
    }

    /**
     * 返回用户的所有视频列表 带缩略图 视频时长 info信息
     * @param params
     * @return
     */
    @Operation(summary = "查询用户视频列表", description = "返回用户的所有视频列表,包含缩略图、视频时长和举报信息")
    @RequestMapping(value = "/findListByUser",method = RequestMethod.GET)
    public R findListByUser(@RequestParam Map<String, Object> params){
        boolean b = Optional.ofNullable(params).isPresent();
        if(!b){
            return R.error("参数不能为空!");
        }
        PageUtils page = infoService.findListByUser(params);
        /**
         * 获取每个状态下的条数 返回
         */
        Map<String, Object> counts  = new HashMap<>();
        List<InfoEntity> list = infoService.findListByUserAll(params);
        long count1 = list.stream().filter(s -> s.getReviewStatus() == 0).count();
        long count2 = list.stream().filter(s -> s.getReviewStatus() == 2).count();
        long count3 = list.stream().filter(s -> s.getReviewStatus() == 3).count();
        counts.put("allConut",page.getTotalCount());
        counts.put("count1",count1);
        counts.put("count2",count2);
        counts.put("count3",count3);
        return R.ok("查询成功!").put("page",page).put("counts",counts);
    }

    /**
     * 返回所有视频列表 带缩略图 视频时长 info信息
     * @param params
     * @return
     */
    @Operation(summary = "查询所有视频列表", description = "返回所有视频列表,包含缩略图、视频时长和举报信息")
    @RequestMapping(value = "/findListAll",method = RequestMethod.GET)
    public R findListAll (@RequestParam Map<String, Object> params){
        boolean b = Optional.ofNullable(params).isPresent();
        if(!b){
            return R.error("参数不能为空!");
        }
        PageUtils page = infoService.findListAll(params);
        /**
         * 获取每个状态下的条数 返回
         */
        Map<String, Object> counts  = new HashMap<>();
        List<InfoEntity> list = infoService.findListByUserAll(params);
        long count1 = list.stream().filter(s -> s.getReviewStatus() == 0).count();
        long count2 = list.stream().filter(s -> s.getReviewStatus() == 2).count();
        long count3 = list.stream().filter(s -> s.getReviewStatus() == 3).count();
        counts.put("allConut",page.getTotalCount());
        counts.put("count1",count1);
        counts.put("count2",count2);
        counts.put("count3",count3);
        return R.ok("查询成功!").put("page",page).put("counts",counts);
    }
}
