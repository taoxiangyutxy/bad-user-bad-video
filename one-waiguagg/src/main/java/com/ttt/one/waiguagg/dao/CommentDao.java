package com.ttt.one.waiguagg.dao;

import com.ttt.one.waiguagg.entity.CommentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttt.one.waiguagg.vo.CommentsVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 外挂评论表
 * 
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-11-24 16:23:16
 */
@Mapper
public interface CommentDao extends BaseMapper<CommentEntity> {

    List<CommentEntity> commentsList(Long infoId);
}
