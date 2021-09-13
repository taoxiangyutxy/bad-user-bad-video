package com.ttt.one.fileServer.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;

import com.ttt.one.fileServer.dao.ChunkDao;
import com.ttt.one.fileServer.entity.ChunkEntity;
import com.ttt.one.fileServer.service.ChunkService;


@Service("chunkService")
public class ChunkServiceImpl extends ServiceImpl<ChunkDao, ChunkEntity> implements ChunkService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ChunkEntity> page = this.page(
                new Query<ChunkEntity>().getPage(params),
                new QueryWrapper<ChunkEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ChunkEntity> checkChunk(String identifier, Integer chunkNumber) {
        List<ChunkEntity> chunkEntities = this.baseMapper.selectList(new QueryWrapper<ChunkEntity>()
                .eq("identifier", identifier));
        return chunkEntities;
    }

    @Override
    public void deleteByChunkIdentifier(String identifier) {
        this.baseMapper.delete(new QueryWrapper<ChunkEntity>().eq("identifier",identifier));
    }

    @Override
    public List<ChunkEntity> getByIdentifier(String identifier) {
        List<ChunkEntity> chunkEntities = this.baseMapper.selectList(new QueryWrapper<ChunkEntity>()
                .eq("identifier", identifier));
        return chunkEntities;
    }

    @Override
    public void saveOrUpdateByChunk(ChunkEntity chunk) {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<ChunkEntity>()
                .eq("chunk_number", chunk.getChunkNumber())
                .eq("identifier", chunk.getIdentifier()));
        if(count>0){
            this.updateById(chunk);
        }else{
            this.save(chunk);
        }
    }

    @Override
    public void deleAllByTask() {
        this.baseMapper.delete(new QueryWrapper<>());
    }

}