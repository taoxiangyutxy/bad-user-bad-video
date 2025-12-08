package com.ttt.one.fileServer.service.impl;

import com.ttt.one.fileServer.service.ChunkService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;

import com.ttt.one.fileServer.dao.FileInfoDao;
import com.ttt.one.fileServer.entity.FileInfoEntity;
import com.ttt.one.fileServer.service.FileInfoService;
import org.springframework.transaction.annotation.Transactional;


@Service("fileInfoService")
public class FileInfoServiceImpl extends ServiceImpl<FileInfoDao, FileInfoEntity> implements FileInfoService {

    @Autowired
    private ChunkService chunkService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<FileInfoEntity> page = this.page(
                new Query<FileInfoEntity>().getPage(params),
                new QueryWrapper<FileInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public FileInfoEntity checkFileInfo(String identifier) {
        FileInfoDao infoDao = this.getBaseMapper();
        FileInfoEntity infoEntity = infoDao.selectOne(new QueryWrapper<FileInfoEntity>().eq("identifier", identifier));
        return infoEntity;
    }

    @Override
    public void updateFileInfo(Long waiguaInfoId,String cover, List<String> identifiers) {
        FileInfoDao infoDao = this.getBaseMapper();
        infoDao.updateByIdentifiers(waiguaInfoId,new Date(),cover, identifiers);
    }
    //@Transactional
    @Override
    public void deleAllIn(Long infoId) {
        FileInfoDao infoDao = this.getBaseMapper();
        List<FileInfoEntity> infoEntities = infoDao.selectList(new QueryWrapper<FileInfoEntity>().eq("waigua_info_id", infoId));
        for (FileInfoEntity infoEntity : infoEntities) {
            //删除所有分片
            chunkService.deleteByChunkIdentifier(infoEntity.getIdentifier());
            //删除文件信息
            infoDao.deleteById(infoEntity.getId());
        }
        //测试分布式事务seata
        //throw new RuntimeException("删除所有文件信息失败");

    }

    @Override
    public List<FileInfoEntity> videoList(Long id) {
        FileInfoDao infoDao = this.getBaseMapper();
        List<FileInfoEntity> infoEntities = infoDao.selectList(new QueryWrapper<FileInfoEntity>().eq("waigua_info_id", id));
        return infoEntities;
    }

    @Override
    public FileInfoEntity saveFile(FileInfoEntity entity) {
        this.baseMapper.saveFile(entity);
        return entity;
    }

    @Override
    public List<FileInfoEntity> listByPass() {
        return this.baseMapper.listByPass();
    }

    @Override
    public List<FileInfoEntity> listByPassAndExpired() {
        return this.baseMapper.listByPassAndExpired();
    }

}