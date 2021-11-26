package com.ttt.one.waiguagg.service.impl;

import com.ttt.one.waiguagg.vo.CommentsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;

import com.ttt.one.waiguagg.dao.CommentDao;
import com.ttt.one.waiguagg.entity.CommentEntity;
import com.ttt.one.waiguagg.service.CommentService;


@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentDao, CommentEntity> implements CommentService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CommentEntity> page = this.page(
                new Query<CommentEntity>().getPage(params),
                new QueryWrapper<CommentEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CommentsVO> commentsList(Long infoId) {
        List<CommentEntity> comments =this.baseMapper.commentsList(infoId);
        List<CommentsVO> commentsVOS = new ArrayList<>();

        for (CommentEntity comment : comments) {
            if(comment.getParentId()==0){ //父级
                /**
                 * 一级评论
                 */
                CommentsVO vo = new CommentsVO();
                BeanUtils.copyProperties(comment,vo);
                /**
                 * 二级评论
                 */
                List<CommentEntity> collect = comments.stream().map(commentEntity -> {
                    if (commentEntity.getParentId().equals(comment.getId())) {
                        return commentEntity;
                    }
                    return null;
                }).filter(commentEntity -> commentEntity!=null).collect(Collectors.toList());
                vo.setChildComments(collect);
                vo.setCommentCount(collect.size());
                commentsVOS.add(vo);
            }
        }
        return commentsVOS;
    }

    public void findChildren(CommentEntity parent, List<CommentEntity> fatherChildren) {

        // 找出直接子级
        List<CommentEntity> comments = parent.getChildren();

        // 遍历直接子级的子级
        for (CommentEntity comment : comments) {

            // 若非空，则还有子级，递归
            if (!comment.getChildren().isEmpty()) {
                findChildren(comment, fatherChildren);
            }

            // 已经到了最底层的嵌套关系，将该回复放入新建立的集合
            fatherChildren.add(comment);

            // 容易忽略的地方：将相对底层的子级放入新建立的集合之后
            // 则表示解除了嵌套关系，对应的其父级的子级应该设为空
            comment.setChildren(new ArrayList<>());
        }
    }
}