package com.ttt.one.waiguagg.utils;

import com.ttt.one.common.exception.BizException;
import com.ttt.one.common.utils.constant.InfoConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 *点赞工具类
 */
@Slf4j
public class GiveLikeUtil {
    @Autowired
    private static StringRedisTemplate redisTemplate;
    /**
     *  描述: 统计评论的总点赞数
     * @param relationId:
     * @return Long
     * @author txy
     * @description
     * @date 2021/11/9 16:02
     */
    public static synchronized Long countRelationLike(Long relationId, Integer type) {
        validateParam(relationId);
        String relationLikedResult = (String) redisTemplate.opsForHash().get(InfoConstant.TOTAL_LIKE_COUNT_KEY, relationId+"::"+type);
        Long likeCount = 0L;
        if(!org.apache.commons.lang.StringUtils.isEmpty(relationLikedResult)){
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
    public static  Integer whetherThumbUp(Long relationId,Long likedUserId,Integer type) {
        /**
         * 先去缓存  缓存有 直接按缓存的 return
         */
        //获取缓存值
        String value =(String) redisTemplate.opsForHash()
                .get(InfoConstant.INFO_LIKED_USER_KEY, relationId + "::" + likedUserId+"::"+type);
        if(!org.apache.commons.lang.StringUtils.isEmpty(value)){
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
    private static void validateParam(Long... params) {
        for (Long param : params) {
            if (null == param) {
                log.error("入参存在null值");
                throw new BizException("参数不能为null!");
            }
        }
    }
}

