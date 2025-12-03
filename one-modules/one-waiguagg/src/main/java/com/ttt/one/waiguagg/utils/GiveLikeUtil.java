package com.ttt.one.waiguagg.utils;

import com.ttt.one.common.exception.BizException;
import com.ttt.one.common.utils.constant.InfoConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 点赞工具类
 */
@Slf4j
@Component
public class GiveLikeUtil {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 统计评论的总点赞数
     *
     * @param relationId 关联ID
     * @param type       类型
     * @return 点赞总数
     */
    public synchronized Long countRelationLike(Long relationId, Integer type) {
        validateParam(relationId);
        String relationLikedResult = (String) redisTemplate.opsForHash().get(
                InfoConstant.TOTAL_LIKE_COUNT_KEY, 
                relationId + "::" + type);
        
        if (relationLikedResult != null && !relationLikedResult.isEmpty()) {
            try {
                return Long.parseLong(relationLikedResult);
            } catch (NumberFormatException e) {
                log.warn("解析点赞数失败: {}", relationLikedResult);
                return 0L;
            }
        }
        return 0L;
    }
    /**
     * 检查用户是否对评论点过赞
     *
     * @param relationId  关联ID
     * @param likedUserId 用户ID
     * @param type        类型
     * @return 点赞状态(1-已点赞, 0-未点赞, null-未找到)
     */
    public Integer whetherThumbUp(Long relationId, Long likedUserId, Integer type) {
        // 先从缓存中查找
        String value = (String) redisTemplate.opsForHash()
                .get(InfoConstant.INFO_LIKED_USER_KEY, 
                     relationId + "::" + likedUserId + "::" + type);
        
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                log.warn("解析点赞状态失败: {}", value);
                return null;
            }
        }
        
        // 缓存中没有，需要从数据库查询（当前被注释掉）
          /*likeCount  = givelikeService.getBaseMapper().selectCount(new QueryWrapper<GivelikeEntity>()
                .eq("relation_id",relationId).eq("user_id",likedUserId)
                .eq("type",type).eq("del_flag",Constant.STATUS_0));*/
        return null;
    }
    /**
     * 入参验证
     *
     * @param params 参数数组
     */
    private void validateParam(Long... params) {
        for (Long param : params) {
            if (param == null) {
                log.error("入参存在null值");
                throw new BizException("参数不能为null!");
            }
        }
    }
}

