package com.ttt.one.waiguagg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.waiguagg.entity.InfoEntity;
import com.ttt.one.waiguagg.vo.VideoPreviewVO;
import com.ttt.one.waiguagg.vo.WaiGuaInfoVO;

import java.util.List;
import java.util.Map;

/**
 * 一个外挂账号，会有多个举报信息,直到被永封该账号不会再接受新的举报信息。
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-09 10:17:14
 */
public interface InfoService extends IService<InfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查全部所有
     * @param params
     * @return
     */
    PageUtils queryPageAll(Map<String, Object> params);

    /**
     * 保存 外挂账号及外挂信息表
     * @param waiGuaInfoVO
     */
    void saveUnmberAndInfo(WaiGuaInfoVO waiGuaInfoVO);

    /**
     * 返回外挂账号 及 外挂信息
     * @param id
     * @param currentUser 当前用户id
     * @return
     */
    WaiGuaInfoVO getByIdAndUnmber(Long id,Long currentUser);

    /**
     * 更新外挂账号  及  外挂信息表
     * @param waiGuaInfoVO
     */
    void updateByIdAndUnmber(WaiGuaInfoVO waiGuaInfoVO);

    /**
     * 关联删除该外挂账号下的所有信息，TODO 如果有别人也举报过 则只删除自己
     * @param asList
     */
    void removeByIdsAllIn(List<Long> asList);

    /**
     * 待审核 列表
     * @param params
     * @param reviewVal
     * @return
     */
    PageUtils queryPageAllByReview(Map<String, Object> params, Long reviewVal);

    /**
     * 更新审核状态
     * @param waiGuaInfoVO
     */
    void updateByIdAndReview(WaiGuaInfoVO waiGuaInfoVO);

    /**
     * 根据外挂信息id  返回视频预览信息列表  需调用远程服务
     * @param id
     * @return
     */
    List<VideoPreviewVO> videolistByInfoId(Long id);

    /**
     * 门户网站信息列表
     * @param params
     * @return
     */
    List<WaiGuaInfoVO>  pageAllWaiGua(Map<String, Object> params);

    /**
     *  描述: 点赞
     * @param relationId: 被点赞对象id
     * @param likedUserId: 点赞用户
     * @param type: 点赞对象类型
     * @return void
     * @author txy
     * @description
     * @date 2021/11/9 15:02
     */
    void giveLikeInfo(Long relationId,Long likedUserId,Integer type);
    /**
     *  描述: 取消点赞
     * @param relationId: 被点赞对象id
     * @param likedUserId: 点赞用户
     * @param type: 点赞对象类型
     * @return void
     * @author txy
     * @description
     * @date 2021/11/9 15:02
     */
    void unGiveLikeInfo(Long relationId, Long likedUserId,Integer type);
    /**
     *  描述: redis定时任务同步数据库 加事务
     * @param :
     * @return void
     * @author txy
     * @description
     * @date 2021/11/12 10:40
     */
    void redisDataToMysql();
    /**
     *  描述: 保存外挂账号 外挂信息 以及关联视频文件
     * @param waiGuaInfoVO:
     * @return void
     * @author txy
     * @description
     * @date 2021/12/24 16:44
     */
    void saveAndUpdateFile(WaiGuaInfoVO waiGuaInfoVO);

    /**
     * 返回用户的所有视频列表 带缩略图 视频时长 info信息
     * @param params
     * @return
     */
    PageUtils findListByUser(Map<String, Object> params);

    /**
     * 返回用户的所有视频列表 带缩略图 视频时长 info信息   不分页
     * @param params
     * @return
     */
    List<InfoEntity> findListByUserAll(Map<String, Object> params);
}

