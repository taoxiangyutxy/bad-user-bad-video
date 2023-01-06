package com.ttt.one.fileServer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.fileServer.entity.FileInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-13 17:41:45
 */
public interface FileInfoService extends IService<FileInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据identifier  是否上传过文件   秒传验证
     * @param identifier
     * @return
     */
    FileInfoEntity checkFileInfo(String identifier);

    /**
     * 上传视频表单提交
     * @param waiguaInfoId
     * @param identifiers
     */
    void updateFileInfo(Long waiguaInfoId,String cover,List<String> identifiers);

    void deleAllIn(Long infoId);

    /**
     * 根据外挂信息id  返回所有的视频信息
     * @param id
     * @return
     */
    List<FileInfoEntity> videoList(Long id);
    /**
     *  描述: 返回 插入的id
     * @param entity:
     * @return FileInfoEntity
     * @author txy
     * @description
     * @date 2021/12/24 14:14
     */
    FileInfoEntity saveFile(FileInfoEntity entity);

    /**
     * 返回审核通过的视频列表
     * @return
     */
    List<FileInfoEntity> listByPass();
}

