package com.ttt.one.waiguagg.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.ttt.one.waiguagg.vo.CommentsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttt.one.waiguagg.entity.CommentEntity;
import com.ttt.one.waiguagg.service.CommentService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;



/**
 * 外挂评论表
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-11-24 16:23:16
 */
@RestController
@RequestMapping("comment/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @RequestMapping("/commentsList")
    public R commentsList(@RequestParam Long infoId){
        List<CommentsVO>  list= commentService.commentsList(infoId);

        return R.ok().put("list", list);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("comment:comment:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = commentService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("comment:comment:info")
    public R info(@PathVariable("id") Long id){
		CommentEntity comment = commentService.getById(id);

        return R.ok().put("comment", comment);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("comment:comment:save")
    public R save(@RequestBody CommentEntity comment){
        comment.setDelFlag(0);
        comment.setThumbUpNumber(0);
        comment.setOnNumberOf(0);
        comment.setPlacedTheTop(0);
        comment.setCreateTime(new Date());
		commentService.save(comment);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("comment:comment:update")
    public R update(@RequestBody CommentEntity comment){
		commentService.updateById(comment);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
  //  @RequiresPermissions("comment:comment:delete")
    public R delete(@RequestBody Long[] ids){
		commentService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
