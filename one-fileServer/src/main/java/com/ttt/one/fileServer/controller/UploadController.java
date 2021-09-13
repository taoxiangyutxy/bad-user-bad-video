package com.ttt.one.fileServer.controller;

import com.ttt.one.common.utils.FileUploadConstant;
import com.ttt.one.common.utils.R;
import com.ttt.one.fileServer.entity.ChunkEntity;
import com.ttt.one.fileServer.entity.FileInfoEntity;
import com.ttt.one.fileServer.service.ChunkService;
import com.ttt.one.fileServer.service.FileInfoService;
import com.ttt.one.fileServer.task.DynmaicCronExpression;
import com.ttt.one.fileServer.utils.MinIoUtils;
import com.ttt.one.fileServer.vo.FileInfoVO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 上传文件
 * @author ttt
 * @date 2021/8/13
 */
@RestController
@RequestMapping("/fileServer/uploader")
@Slf4j
public class UploadController {
    @Value("${file.save-path}")
    private String uploadFolder;

    private final ChunkService chunkService;
    private final FileInfoService fileInfoService;

    public UploadController(ChunkService chunkService, FileInfoService fileInfoService) {
        this.chunkService = chunkService;
        this.fileInfoService = fileInfoService;
    }
    @Autowired
    private DynmaicCronExpression expression;
    @GetMapping("/setCron")
    public R setCron(String cron){
        log.info("动态修改CRON配置：{}",cron);
        expression.setCron(cron);
        return R.ok();
    }


    /**
     * 根据外挂信息id 返回外挂视频集合
     * @param id
     * @return
     */
    @RequestMapping("/info/{id}")
    public R videoInfo(@PathVariable("id") Long id){
        List<FileInfoEntity> fileList = fileInfoService.videoList(id);
        return R.ok().put("fileList", fileList);
    }

//********************Minio文件服务器********************************

    /**
     * 分片上传文件
     * @param chunk
     * @return
     */
    @PostMapping("/chunk")
    public R uploadChunk(ChunkEntity chunk) {
        MultipartFile file = chunk.getFile();
        log.debug("file originName: {}, chunkNumber: {}", file.getOriginalFilename(), chunk.getChunkNumber());
        try {
            MinIoUtils.uploadFileMinIo(file,chunk.getIdentifier()+"/"+chunk.getChunkNumber()+"-"+chunk.getFilename());
            log.debug("文件 {} 写入成功, uuid:{}", chunk.getFilename(), chunk.getIdentifier());
            chunkService.saveOrUpdateByChunk(chunk);
            return R.ok().put("data","上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传失败");
        }
    }

    /**
     * 是否断点续传   返回有哪些分片已经上传成功了
     * @param chunk
     * @param response
     * @return
     */

    @GetMapping("/chunk")
    public R checkChunk(ChunkEntity chunk, HttpServletResponse response) {
        log.debug("f===: {}, chunkNumber: {}", "----", chunk.getChunkNumber());
        //秒传验证
        if(fileInfoService.checkFileInfo(chunk.getIdentifier())){
            // chunk.setStatus(true);
            return R.ok().put("data",chunk).put("code", FileUploadConstant.UploadFileType.FILE_SUCCESS.getValue());
        }
        //是否有分片  进行断点续传
        List<ChunkEntity> chunkEntities = chunkService.checkChunk(chunk.getIdentifier(), chunk.getChunkNumber());
        if (chunkEntities!=null && chunkEntities.size()>0) {
            // response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            List<Integer> uploaded = chunkEntities.stream().map(ChunkEntity::getChunkNumber).collect(Collectors.toList());
            chunk.setUploaded(uploaded);
        }
        return R.ok().put("data",chunk).put("code", FileUploadConstant.UploadFileType.FILE_BREAKPOINT.getValue());
    }


    /**
     * 合并文件
     * @param fileInfo
     * @return
     */
    @PostMapping("/mergeFile")
    public R mergeFile(@RequestBody FileInfoEntity fileInfo) {
        String filename = fileInfo.getFilename();
        List<ChunkEntity> chunkEntities =  chunkService.getByIdentifier(fileInfo.getIdentifier());
        List<String> chunkNames = new ArrayList<>();
        for (int i = 1; i < chunkEntities.size()+1; i++) {
            chunkNames.add(fileInfo.getIdentifier()+"/"+i+"-"+fileInfo.getFilename());
        }
        boolean b = MinIoUtils.composeObjectAndRemoveChunk("uploadtest", chunkNames, fileInfo.getIdentifier()+"/"+filename);
        String url = MinIoUtils.getObjectUrl("uploadtest", fileInfo.getIdentifier()+"/"+filename, 60 * 24*7);
        fileInfo.setLocation(url);
        fileInfoService.save(fileInfo);
        return R.ok();
    }

//********************Minio文件服务器******************************
    /**
     * 删除所有  根据外挂信息id
     * @param infoId
     * @return
     */
    @PostMapping("/deleAllIn")
    public R deleAllIn(@RequestParam Long infoId){
        fileInfoService.deleAllIn(infoId);
        return R.ok();
    }

    /**
     * 移除上传文件
     * @param chunk
     * @return
     */
    @PostMapping("/deleteFileChunk")
    public R deleteFileChunk(@RequestBody ChunkEntity chunk) {
        chunkService.deleteByChunkIdentifier(chunk.getIdentifier());
        return R.ok();
    }

    @PostMapping("/updateFileInfo")
    public R updateFileInfo(@RequestBody FileInfoVO infoVO) {
        if(infoVO==null || infoVO.getIdentifiers().size()<=0){
            return R.error("请先上传文件");
        }
        fileInfoService.updateFileInfo(infoVO.getId(),infoVO.getIdentifiers());
        return R.ok();
    }

}
