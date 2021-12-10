package com.ttt.one.waiguagg.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.alibaba.fastjson.TypeReference;
import com.ttt.one.common.utils.Constant;
import com.ttt.one.waiguagg.fegin.UserFeginServer;
import com.ttt.one.waiguagg.vo.UserEntityVO;
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

import javax.servlet.http.HttpServletRequest;

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
    @Autowired
    private UserFeginServer userFeginServer;
    @RequestMapping("/commentsList")
    public R commentsList(@RequestParam Long infoId, HttpServletRequest request){
        UserEntityVO userEntityVO = (UserEntityVO) request.getSession().getAttribute(Constant.LOGIN_USER);
        Long currentUser = -1L;
        if(userEntityVO!=null){
            currentUser = userEntityVO.getId();
        }
        List<CommentEntity>  list= commentService.commentsList(infoId,2,currentUser);
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

       /* R r = userFeginServer.info(comment.getUserId());
        if(r.getCode()==0){
            UserEntityVO data = r.getData("user", new TypeReference<UserEntityVO>() {
            });
            //获取当前登录人信息存入
            comment.setNickname(data.getUsername());
        }*/
        //获取被回复人名称
        CommentEntity commentEntity = commentService.selectCommentByCommentId(comment.getParentId());
        if(commentEntity!=null){
            comment.setNickname(commentEntity.getUsername());
        }
        //应该获取当前登录人id set进user_id 字段 该条评论谁评论的：暂时是html写死的
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
