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
     * @return
     */
    WaiGuaInfoVO getByIdAndUnmber(Long id);

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
}

