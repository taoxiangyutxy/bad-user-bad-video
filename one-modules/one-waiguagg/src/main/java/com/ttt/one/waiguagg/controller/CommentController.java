package com.ttt.one.waiguagg.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ttt.one.common.utils.Constant;
import com.ttt.one.oplog.annotation.OperationLog;
import com.ttt.one.oplog.annotation.OperationLogType;
import com.ttt.one.waiguagg.vo.UserEntityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.ttt.one.waiguagg.entity.CommentEntity;
import com.ttt.one.waiguagg.service.CommentService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;

import javax.servlet.http.HttpServletRequest;

/**
 * 外挂评论控制器
 *
 * 提供外挂信息的评论管理功能
 */
@Tag(name = "外挂评论", description = "外挂评论管理")
@RestController
@RequestMapping("/comment/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 获取指定外挂信息的评论列表
     *
     * @param infoId  外挂信息ID
     * @param request HTTP请求对象
     * @return 评论列表
     */
    @Operation(summary = "获取评论列表", description = "根据外挂信息ID获取评论列表")
    @Parameter(name = "infoId", description = "外挂信息ID")
    @GetMapping("/commentsList")
    public R commentsList(@RequestParam Long infoId, HttpServletRequest request) {
        UserEntityVO userEntityVO = (UserEntityVO) request.getSession().getAttribute(Constant.LOGIN_USER);
        Long currentUser = -1L;
        if (userEntityVO != null) {
            currentUser = userEntityVO.getId();
        }
        List<CommentEntity> list = commentService.commentsList(infoId, 2, currentUser);
        return R.ok().put("list", list);
    }


    /**
     * 获取评论分页列表
     *
     * @param params 查询参数
     * @return 评论分页列表
     */
    @Operation(summary = "获取评论分页列表", description = "分页查询评论信息")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = commentService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 根据ID获取评论信息
     *
     * @param id 评论ID
     * @return 评论信息
     */
    @Operation(summary = "根据ID获取评论信息", description = "通过评论ID查询指定评论的详细信息")
    @Parameter(name = "id", description = "评论ID")
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
		CommentEntity comment = commentService.getById(id);

        return R.ok().put("comment", comment);
    }

    /**
     * 保存评论
     *
     * @param comment 评论实体
     * @return 操作结果
     */
    @Operation(summary = "保存评论", description = "创建新的评论信息")
    @OperationLog(desc = "保存评论", type= OperationLogType.ADD)
    @Parameter(name = "comment", description = "评论实体")
    @PostMapping("/save")
    public R save(@RequestBody CommentEntity comment) {
        // 获取被回复人名称
        CommentEntity commentEntity = commentService.selectCommentByCommentId(comment.getParentId());
        if (commentEntity != null) {
            comment.setNickname(commentEntity.getUsername());
        }
        
        // 初始化评论默认值
        comment.setDelFlag(0);
        comment.setThumbUpNumber(0);
        comment.setOnNumberOf(0);
        comment.setPlacedTheTop(0);
        comment.setCreateTime(new Date());
        
        commentService.save(comment);
        return R.ok();
    }

    /**
     * 更新评论
     *
     * @param comment 评论实体
     * @return 操作结果
     */
    @Operation(summary = "更新评论", description = "修改现有评论的信息")
    @Parameter(name = "comment", description = "评论实体")
    @PostMapping("/update")
    public R update(@RequestBody CommentEntity comment) {
		commentService.updateById(comment);

        return R.ok();
    }

    /**
     * 批量删除评论
     *
     * @param ids 评论ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除评论", description = "根据评论ID数组批量删除评论信息")
    @Parameter(name = "ids", description = "评论ID数组")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
		commentService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
