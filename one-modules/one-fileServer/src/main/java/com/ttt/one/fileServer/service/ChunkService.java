package com.ttt.one.fileServer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.fileServer.entity.ChunkEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-13 17:41:45
 */
public interface ChunkService extends IService<ChunkEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 判断库中是否有分片文件数据  验证是否需要断点续传
     * @param identifier
     * @param chunkNumber
     * @return
     */
    List<ChunkEntity> checkChunk(String identifier, Integer chunkNumber);

    /**
     * 主动取消上传   去清空数据库已上传完成的分片记录
     * @param identifier
     */
    void deleteByChunkIdentifier(String identifier);

    /**
     * 根据唯一值返回  所有分片
     * @return
     */
    List<ChunkEntity> getByIdentifier(String identifier);

    /**
     * 代码健壮-避免数据库重复插入分片bug
     * 先查有就更新 没有就插入
     * @param chunk
     */
    void saveOrUpdateByChunk(ChunkEntity chunk);

    /**
     * 定时任务  删除分片数据
     */
    void deleAllByTask();
}

