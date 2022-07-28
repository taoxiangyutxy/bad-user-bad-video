package com.ttt.one.fileServer.controller;

import com.ttt.one.common.utils.FileUploadConstant;
import com.ttt.one.common.utils.R;
import com.ttt.one.fileServer.entity.ChunkEntity;
import com.ttt.one.fileServer.entity.FileInfoEntity;
import com.ttt.one.fileServer.service.ChunkService;
import com.ttt.one.fileServer.service.FileInfoService;
import com.ttt.one.fileServer.task.DynmaicCronExpression;
import com.ttt.one.fileServer.utils.FileUtil;
import com.ttt.one.fileServer.utils.MinIoUtils;
import com.ttt.one.fileServer.vo.FileInfoVO;
import lombok.extern.slf4j.Slf4j;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
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
    @Autowired
    RabbitTemplate rabbitTemplate;

    @ResponseBody
    @RequestMapping("/test")
    public String createUserTest(){
        FileInfoEntity entity = new FileInfoEntity();
        entity.setFilename("测试测试");
        entity.setType("MQMQMQMQMQ");
        entity.setCreateTime(new Date());
        //消息发送给  create.ttt
        rabbitTemplate.convertAndSend("topic-exchange","create.ttt",entity);
        return "ok:"+new Date();
    }


    /**
     * 线程池
     */
  //  public static ExecutorService executor = Executors.newFixedThreadPool(10);
    @Autowired
    private ThreadPoolExecutor executor;

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
        FileInfoEntity infoEntity = fileInfoService.checkFileInfo(chunk.getIdentifier());
        if(infoEntity!=null){
            chunk.setFileInfoId(infoEntity.getId());
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
    public R mergeFile(@RequestBody FileInfoEntity fileInfo) throws Exception {
        String filename = fileInfo.getFilename();
        List<ChunkEntity> chunkEntities =  chunkService.getByIdentifier(fileInfo.getIdentifier());
        List<String> chunkNames = new ArrayList<>();
        for (int i = 1; i < chunkEntities.size()+1; i++) {
            chunkNames.add(fileInfo.getIdentifier()+"/"+i+"-"+fileInfo.getFilename());
        }
        boolean b = MinIoUtils.composeObjectAndRemoveChunk("uploadtest", chunkNames, fileInfo.getIdentifier()+"/"+filename);
        String url = MinIoUtils.getObjectUrl("uploadtest", fileInfo.getIdentifier()+"/"+filename, 60 * 24*7);
        fileInfo.setLocation(url);
        FileInfoEntity file = fileInfoService.saveFile(fileInfo);
        /**
         * 合并完文件  直接把分片数据 删除  TODO
         */


        /**
         * 根据合成文件 进行截屏 多线程5张不同帧数图片上传服务器 并返回预览地址
         */
        List<String> urls = new ArrayList<>();
        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            String s = coverImage(fileInfo.getIdentifier(), 1, filename);
            urls.add(s);
            return 1;
            }, executor);
        CompletableFuture<Integer> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            String s = coverImage(fileInfo.getIdentifier(), 2, filename);
            urls.add(s);
            return 1;
        }, executor);
        CompletableFuture<Integer> future03 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            String s = coverImage(fileInfo.getIdentifier(), 3, filename);
            urls.add(s);
            return 1;
        }, executor);
        CompletableFuture<Integer> future04 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            String s = coverImage(fileInfo.getIdentifier(), 4, filename);
            urls.add(s);
            return 1;
        }, executor);
//        CompletableFuture<Integer> future05 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getId());
//            String s = coverImage(fileInfo.getIdentifier(), 5, filename);
//            urls.add(s);
//            return 1;
//        }, executor);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(future01, future02, future03,future04);
        allOf.get();//阻塞等待  多个线程执行完再继续执行
        file.setCovers(urls);
        log.info("最新插入文件的id:{}",file.getId());
        return R.ok().put("data",file);
    }

    /**
     * 截取图片
     * @param identifier
     * @param i
     * @param filename
     * @return
     */
    private String coverImage(String identifier,Integer i,String filename ){
        File picFile = null;
        try {
            picFile  = File.createTempFile(i*25+identifier, ".jpg");
            FileUtil.imageFrame(identifier + "/" + filename, i*25,picFile);
            log.info("aaa:{}",i*25+identifier);
            MultipartFile mFile = FileUtil.fileTurnMulti(picFile);
            MinIoUtils.uploadFileMinIo(mFile,"images/"+i*25+identifier+".jpg");
            String previewUrl = MinIoUtils.getObjectPreviewUrl("uploadtest",
                    "images/" + i*25+identifier+".jpg", 60 * 24 * 7, "image/jpeg");
            return previewUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
        fileInfoService.updateFileInfo(infoVO.getId(),"",infoVO.getIdentifiers());
        return R.ok();
    }
    /**
     *  描述: 前端上传视频 用
     * @param entity:
     * @return R
     * @author txy
     * @description
     * @date 2021/12/24 17:29
     */
    @PostMapping("/updateFileInfoByWeb")
    public R updateFileInfoByWeb(@RequestBody FileInfoEntity entity) {
        List<String> identifiers = new ArrayList<>();
        identifiers.add(entity.getIdentifier());
        fileInfoService.updateFileInfo(entity.getWaiguaInfoId(),entity.getCover(),identifiers);
        return R.ok();
    }

    /**
     * 上传图片并返回预览地址
     * @param file
     * @return
     */
    @PostMapping("/uploadImage")
    public R uploadImage(MultipartFile file) {
        log.info("file originName: {},type:{}", file.getOriginalFilename(),file.getContentType());
        try {
            MinIoUtils.uploadFileMinIo(file,"images/"+file.getOriginalFilename());
            String previewUrl = MinIoUtils.getObjectPreviewUrl("uploadtest",
                    "images/" + file.getOriginalFilename(), 60 * 24 * 7, file.getContentType());
            return R.ok().put("data",previewUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传失败");
        }
    }
}
