package com.ttt.one.fileServer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttt.one.fileServer.entity.FileInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 
 * 
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-13 17:41:45
 */
@Mapper
public interface FileInfoDao extends BaseMapper<FileInfoEntity> {
    /**
     *批量关联外挂账号
     * @param waiguaInfoId
     * @param createTime
     * @param identifiers
     */
    void updateByIdentifiers(@Param("waiguaInfoId") Long waiguaInfoId, @Param("createTime") Date createTime,@Param("cover") String cover, @Param("identifiers") List<String> identifiers);

    void saveFile(FileInfoEntity entity);

    /**
     * 返回审核通过的视频列表
     * @return
     */
    List<FileInfoEntity> listByPass();
}
