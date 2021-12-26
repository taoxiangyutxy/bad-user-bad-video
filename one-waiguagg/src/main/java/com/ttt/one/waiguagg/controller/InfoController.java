package com.ttt.one.waiguagg.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;
import com.ttt.one.waiguagg.entity.GivelikeEntity;
import com.ttt.one.waiguagg.vo.VideoPreviewVO;
import com.ttt.one.waiguagg.vo.WaiGuaInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ttt.one.waiguagg.entity.InfoEntity;
import com.ttt.one.waiguagg.service.InfoService;

/**
 * 一个外挂账号，会有多个举报信息,直到被永封该账号不会再接受新的举报信息。
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-09 10:17:14
 */
@RestController
@RequestMapping("waiguagg/info")
public class InfoController {
    @Autowired
    private InfoService infoService;

    /**
     * 返回视频预览 信息列表  根据外挂信息id
     */
    @RequestMapping("/videoList/{id}")
    public R videoList(@PathVariable("id") Long id){
        List<VideoPreviewVO> videolist = infoService.videolistByInfoId(id);
        return R.ok().put("videolist", videolist);
    }


    /**
     * 更新审核状态
     * @param waiGuaInfoVO
     * @return
     */
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
    @GetMapping("/listReview/{reviewVal}")
    public R listReview(@RequestParam Map<String, Object> params,@PathVariable("reviewVal")Long reviewVal){
        PageUtils page = infoService.queryPageAllByReview(params,reviewVal);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("waiguagg:info:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = infoService.queryPageAll(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("waiguagg:info:info")
    public R info(@PathVariable("id") Long id){
        WaiGuaInfoVO infoVO = infoService.getByIdAndUnmber(id,-1L);
        return R.ok().put("info", infoVO);
    }

    /**
     * 保存   管理员管理平台
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("waiguagg:info:save")
    public R save(@RequestBody WaiGuaInfoVO waiGuaInfoVO){
		infoService.saveUnmberAndInfo(waiGuaInfoVO);

        return R.ok();
    }
    /**
     * 保存   前台
     */
    @RequestMapping("/saveAndUpdateFile")
    public R saveAndUpdateFile(@RequestBody WaiGuaInfoVO waiGuaInfoVO){
        infoService.saveAndUpdateFile(waiGuaInfoVO);
        return R.ok();
    }
    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("waiguagg:info:update")
    public R update(@RequestBody WaiGuaInfoVO waiGuaInfoVO){
		infoService.updateByIdAndUnmber(waiGuaInfoVO);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("waiguagg:info:delete")
    public R delete(@RequestBody Long[] ids){
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
    @RequestMapping(value = "/unGiveLikeInfo",method = RequestMethod.POST)
    public R unGiveLikeInfo(@RequestBody GivelikeEntity givelikeEntity){
        infoService.unGiveLikeInfo(givelikeEntity.getRelationId(),givelikeEntity.getUserId(),givelikeEntity.getType());
        return R.ok("取消点赞成功");
    }
}
