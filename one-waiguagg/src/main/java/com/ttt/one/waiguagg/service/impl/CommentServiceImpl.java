package com.ttt.one.waiguagg.service.impl;

import com.ttt.one.common.exception.RRException;
import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.utils.constant.InfoConstant;
import com.ttt.one.waiguagg.utils.GiveLikeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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

@Slf4j
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentDao, CommentEntity> implements CommentService {
    @Autowired
    private  StringRedisTemplate redisTemplate;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CommentEntity> page = this.page(
                new Query<CommentEntity>().getPage(params),
                new QueryWrapper<CommentEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CommentEntity> commentsList(Long infoId, Integer type,Long currentUser) {
        /**
         * 默认父级id 0L
         */
        List<CommentEntity> list = this.baseMapper.selectCommentById(infoId,0L,currentUser);
        for (CommentEntity comment : list) {
            List<CommentEntity> childless = new ArrayList<>();
            findChildren(comment,childless);
            /**
             * 孩子时间字段排序  必须有值
             */
            childless.sort(Comparator.comparing(CommentEntity::getCreateTime));
            comment.setChildren(childless);
        }
        /**
         * 最后进行数据汇总
         */
        list=  list.stream().map(comment -> {
            dataSummary(currentUser, comment);
            for (CommentEntity child : comment.getChildren()) {
                dataSummary(currentUser, child);
            }
            return comment;
        }).collect(Collectors.toList());
        /**
         * 父级排序
         */
        list.sort(Comparator.comparing(CommentEntity::getCreateTime));
        return list;
    }
    /**
     *  描述: 汇总数据
     * @param currentUser:
     * @param comment:
     * @return void
     * @author txy
     * @description
     * @date 2021/12/10 10:56
     */
    private void dataSummary(Long currentUser, CommentEntity comment) {
        /**
         *汇总缓存点赞数
         */
        Long countRelationLike = countRelationLike(comment.getId(), Constant.LIKETYPE_COMMENT);
        Long countRelationLikeDb = 0L;
        if(comment.getThumbUpNumber()!=null){
            countRelationLikeDb =  Long.valueOf(comment.getThumbUpNumber());
        }
        Integer coutLike = Math.toIntExact(countRelationLike + countRelationLikeDb);
        comment.setThumbUpNumber(coutLike);
        /**
         * 缓存是否点过赞了
         */
        Integer isSupport = whetherThumbUp(comment.getId(), currentUser, Constant.LIKETYPE_COMMENT);
        if(isSupport!=null){
            comment.setIsSupport(isSupport);
        }
    }

    @Override
    public CommentEntity selectCommentByCommentId(Long parentId) {

        return this.baseMapper.selectCommentByCommentId(parentId);
    }

    /**
     *  描述:  把该id下的所有层级的子级全放在fatherChildren集合中
     * @param parent:
     * @param fatherChildren:
     * @return void
     * @author txy
     * @description
     * @date 2021/11/29 10:37
     */
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


    /**
     *  描述: 统计评论的总点赞数
     * @param relationId:
     * @return Long
     * @author txy
     * @description
     * @date 2021/11/9 16:02
     */
    public  synchronized Long countRelationLike(Long relationId, Integer type) {
        validateParam(relationId);
        String relationLikedResult = (String) redisTemplate.opsForHash().get(InfoConstant.TOTAL_LIKE_COUNT_KEY, relationId+"::"+type);
        Long likeCount = 0L;
        if(!org.apache.commons.lang3.StringUtils.isEmpty(relationLikedResult)){
            likeCount = Long.parseLong(relationLikedResult);
            if (likeCount == null) {
                return 0L;
            }
        }
        return likeCount;
    }
    /**
     *  描述: 该用户对该评论是否点过赞
     * @param relationId:
     * @param likedUserId:
     * @param type:
     * @return Integer
     * @author txy
     * @description
     * @date 2021/11/22 14:41
     */
    public   Integer whetherThumbUp(Long relationId,Long likedUserId,Integer type) {
        /**
         * 先去缓存  缓存有 直接按缓存的 return
         */
        //获取缓存值
        String value =(String) redisTemplate.opsForHash()
                .get(InfoConstant.INFO_LIKED_USER_KEY, relationId + "::" + likedUserId+"::"+type);
        if(!org.apache.commons.lang3.StringUtils.isEmpty(value)){
            return Integer.valueOf(value);
        }
        /**
         * 缓存没有  去数据库查
         */
        /*likeCount  = givelikeService.getBaseMapper().selectCount(new QueryWrapper<GivelikeEntity>()
                .eq("relation_id",relationId).eq("user_id",likedUserId)
                .eq("type",type).eq("del_flag",Constant.STATUS_0));*/
        return null;
    }
    /**
     *  描述: 入参验证
     * @param params:
     * @return void
     * @author txy
     * @description
     * @date 2021/11/9 16:03
     */
    private  void validateParam(Long... params) {
        for (Long param : params) {
            if (null == param) {
                log.error("入参存在null值");
                throw new RRException("参数不能为null!");
            }
        }
    }
}