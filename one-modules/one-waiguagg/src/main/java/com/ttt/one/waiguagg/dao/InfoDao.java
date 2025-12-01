package com.ttt.one.waiguagg.dao;

import com.ttt.one.waiguagg.dto.InfoDTO;
import com.ttt.one.waiguagg.entity.InfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 一个外挂账号，会有多个举报信息,直到被永封该账号不会再接受新的举报信息。
 * 
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-09 10:17:14
 */
@Mapper
public interface InfoDao extends BaseMapper<InfoEntity> {

    List<InfoEntity> findListAll(@Param("key") String key, @Param("reviewStatus") String reviewStatus,@Param("currentUser") Long currentUser,@Param("type") Integer type);

    InfoEntity getByIdAndCuser(@Param("id") Long id,@Param("currentUser") Long currentUser,@Param("type") Integer type);

    void saveInfoReturnId(InfoEntity infoEntity);
    /**
     * 返回用户的所有视频列表 带缩略图 视频时长 info信息
     * @param infoDTO
     * @return
     */
    List<InfoEntity> findListByUser(InfoDTO infoDTO);
}
