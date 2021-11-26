package com.ttt.one.waiguagg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.waiguagg.entity.CommentEntity;
import com.ttt.one.waiguagg.vo.CommentsVO;

import java.util.List;
import java.util.Map;

/**
 * 外挂评论表
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-11-24 16:23:16
 */
public interface CommentService extends IService<CommentEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CommentsVO> commentsList(Long infoId);
}

