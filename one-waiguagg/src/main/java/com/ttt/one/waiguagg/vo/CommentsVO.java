package com.ttt.one.waiguagg.vo;

import com.ttt.one.waiguagg.entity.CommentEntity;
import lombok.Data;

import java.util.List;

/**
 * 完整的评论大数据
 */
@Data
public class CommentsVO extends CommentEntity {
    /**
     * 二层评论数据
     */
    private  List<CommentEntity>  childComments;
    /**
     * 二层评论数
     */
    private Integer commentCount;
}
