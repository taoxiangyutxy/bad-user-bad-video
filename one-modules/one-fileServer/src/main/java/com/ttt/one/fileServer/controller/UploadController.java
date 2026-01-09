package com.ttt.one.fileServer.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文件上传控制器
 * 支持分片上传、断点续传、秒传等功能
 * 
 * @author ttt
 * @date 2021/8/13
 */
@Tag(name = "文件上传管理", description = "文件上传、分片上传、断点续传相关接口")
@RestController
@RequestMapping("/fileServer/uploader")
@Slf4j
@EnableAsync
public class UploadController {

    private static final String TOPIC_EXCHANGE = "topic-exchange";
    private static final String ROUTING_KEY_CREATE = "create.ttt";
    private static final String BUCKET_NAME = "uploadtest";
    private static final int URL_EXPIRY_DAYS = 7;
    private static final int COVER_IMAGE_COUNT = 4;

    private final RabbitTemplate rabbitTemplate;
    private final ThreadPoolExecutor executor;
    private final ChunkService chunkService;
    private final FileInfoService fileInfoService;
    private final DynmaicCronExpression expression;

    @Value("${file.save-path}")
    private String uploadFolder;

    /**
     * 构造函数注入
     */
    public UploadController(RabbitTemplate rabbitTemplate,
                          ThreadPoolExecutor executor,
                          ChunkService chunkService,
                          FileInfoService fileInfoService,
                          DynmaicCronExpression expression) {
        this.rabbitTemplate = rabbitTemplate;
        this.executor = executor;
        this.chunkService = chunkService;
        this.fileInfoService = fileInfoService;
        this.expression = expression;
    }

    /**
     * 测试MQ消息发送
     */
    @Operation(summary = "测试MQ消息", description = "测试RabbitMQ消息发送功能")
    @GetMapping("/test")
    public R createUserTest() {
        FileInfoEntity entity = new FileInfoEntity();
        entity.setFilename("测试文件");
        entity.setType("TEST");
        entity.setCreateTime(new Date());
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, ROUTING_KEY_CREATE, entity);
        log.info("MQ测试消息已发送");
        return R.ok("消息发送成功").put("time", new Date());
    }
    
    /**
     * 动态修改定时任务Cron表达式
     */
    @Operation(summary = "动态修改Cron表达式", description = "动态修改定时任务的Cron表达式")
    @GetMapping("/setCron")
    public R setCron(@Parameter(description = "Cron表达式") @RequestParam String cron) {
        if (!StringUtils.hasText(cron)) {
            return R.error("Cron表达式不能为空");
        }
        log.info("动态修改CRON配置:{}", cron);
        expression.setCron(cron);
        return R.ok("Cron表达式修改成功");
    }

    /**
     * 压力测试接口
     */
    @Operation(summary = "压力测试接口", description = "用于压力测试的接口")
    @GetMapping("/testA/{pid}")
    public R createUserTestA(@Parameter(description = "文件ID") @PathVariable("pid") Long pid) {
        FileInfoEntity fileInfo = fileInfoService.getById(pid);
        if (fileInfo == null) {
            return R.error("文件不存在");
        }
        log.info("文件ID:{}, 名称:{}", pid, fileInfo.getFilename());
        // 模拟网络延时
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            log.error("线程中断异常", e);
            Thread.currentThread().interrupt();
        }
        log.info("其他操作完成");
        return R.ok("测试成功").put("time", new Date());
    }

    /**
     * 高并发测试消息
     */
    @Operation(summary = "高并发测试", description = "高并发场景下的问题测试")
    @GetMapping("/order/message")
    public R message() {
        return R.ok("高并发测试接口");
    }
    /**
     * 根据外挂信息ID获取关联的视频列表
     */
    @Operation(summary = "获取外挂视频列表", description = "根据外挂信息ID获取关联的所有视频文件")
    @GetMapping("/info/{id}")
    public R videoInfo(@Parameter(description = "外挂信息ID") @PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return R.error("无效的外挂信息ID");
        }
        List<FileInfoEntity> fileList = fileInfoService.videoList(id);
        return R.ok().put("fileList", fileList);
    }

//********************Minio文件服务器********************************

    /**
     * 分片上传文件
     */
    @Operation(summary = "分片上传", description = "上传文件分片,支持大文件分片上传")
    @PostMapping("/chunk")
    public R uploadChunk(ChunkEntity chunk) {
        if (chunk == null || chunk.getFile() == null) {
            return R.error("分片文件不能为空");
        }
        MultipartFile file = chunk.getFile();
        log.info("上传分片 - 文件名:{}, 分片号:{}/{}", 
                file.getOriginalFilename(), chunk.getChunkNumber(), chunk.getTotalChunks());
        
        try {
            String objectName = String.format("%s/%d-%s", 
                    chunk.getIdentifier(), chunk.getChunkNumber(), chunk.getFilename());
            MinIoUtils.uploadFileMinIo(file, objectName);
            chunkService.saveOrUpdateByChunk(chunk);
            log.info("分片上传成功 - 文件:{}, UUID:{}", chunk.getFilename(), chunk.getIdentifier());
            return R.ok("分片上传成功");
        } catch (Exception e) {
            log.error("分片上传失败 - 文件:{}, 分片号:{}", chunk.getFilename(), chunk.getChunkNumber(), e);
            return R.error("分片上传失败: " + e.getMessage());
        }
    }

    /**
     * 检查文件上传状态
     * 支持秒传和断点续传功能
     */
    @Operation(summary = "检查上传状态", description = "检查文件是否已上传(秒传)或获取已上传的分片列表(断点续传)")
    @GetMapping("/chunk")
    public R checkChunk(ChunkEntity chunk, HttpServletResponse response) {
        if (chunk == null || !StringUtils.hasText(chunk.getIdentifier())) {
            return R.error("文件标识符不能为空");
        }
        
        log.info("检查文件上传状态 - 标识符:{}, 分片号:{}", chunk.getIdentifier(), chunk.getChunkNumber());
        
        // 1. 秒传验证 - 检查文件是否已完整上传
        FileInfoEntity existingFile = fileInfoService.checkFileInfo(chunk.getIdentifier());
        if (existingFile != null) {
            log.info("文件已存在,支持秒传 - 标识符:{}", chunk.getIdentifier());
            chunk.setFileInfoId(existingFile.getId());
            return R.ok("文件已存在")
                    .put("data", chunk)
                    .put("code", FileUploadConstant.UploadFileType.FILE_SUCCESS.getValue());
        }
        
        // 2. 断点续传 - 检查已上传的分片
        List<ChunkEntity> uploadedChunks = chunkService.checkChunk(chunk.getIdentifier(), chunk.getChunkNumber());
        if (!CollectionUtils.isEmpty(uploadedChunks)) {
            List<Integer> uploadedNumbers = uploadedChunks.stream()
                    .map(ChunkEntity::getChunkNumber)
                    .collect(Collectors.toList());
            chunk.setUploaded(uploadedNumbers);
            log.info("找到已上传分片 - 标识符:{}, 分片数:{}", chunk.getIdentifier(), uploadedNumbers.size());
        }
        
        return R.ok("可以继续上传")
                .put("data", chunk)
                .put("code", FileUploadConstant.UploadFileType.FILE_BREAKPOINT.getValue());
    }


    /**
     * 合并文件分片
     * 将所有上传的分片合并为完整文件,并生成视频封面
     */
    @Operation(summary = "合并文件分片", description = "合并所有分片为完整文件,生成访问URL和视频封面")
    @PostMapping("/mergeFile")
    public R mergeFile(@RequestParam(value = "file") MultipartFile file,
                      @RequestParam("filename") String filename,
                      @RequestParam("identifier") String identifier,
                      @RequestParam("totalSize") Long totalSize,
                      @RequestParam("type") String type,
                      @RequestParam("audioDuration") String audioDuration) {
        
        // 参数校验
        if (!StringUtils.hasText(filename) || !StringUtils.hasText(identifier)) {
            return R.error("文件名和标识符不能为空");
        }
        
        long startTime = System.currentTimeMillis();
        log.info("开始合并文件 - 文件名:{}, 标识符:{}", filename, identifier);
        
        try {
            // 构建文件信息
            FileInfoEntity fileInfo = buildFileInfo(filename, identifier, totalSize, type, audioDuration);
            
            // 获取所有分片
            List<ChunkEntity> chunkEntities = chunkService.getByIdentifier(identifier);
            if (CollectionUtils.isEmpty(chunkEntities)) {
                return R.error("未找到文件分片,请先上传文件");
            }
            
            // 构建分片文件名列表
            List<String> chunkNames = buildChunkNames(chunkEntities, identifier, filename);
            
            // 合并分片并删除临时分片文件
            String mergedObjectName = identifier + "/" + filename;
            boolean mergeSuccess = MinIoUtils.composeObjectAndRemoveChunk(BUCKET_NAME, chunkNames, mergedObjectName);
            if (!mergeSuccess) {
                return R.error("文件合并失败");
            }
            
            // 生成文件访问URL
            String fileUrl = MinIoUtils.getObjectUrl(BUCKET_NAME, mergedObjectName, 60 * 24 * URL_EXPIRY_DAYS);
            fileInfo.setLocation(fileUrl);

            // 保存文件信息
            FileInfoEntity savedFile = new FileInfoEntity();
            FileInfoEntity oldFileInfo = fileInfoService.getOne(new QueryWrapper<FileInfoEntity>().eq("identifier", fileInfo.getIdentifier()));
            if(ObjectUtils.isEmpty(oldFileInfo)){
                savedFile =  fileInfoService.saveFile(fileInfo);
            }else{
                fileInfo.setId(oldFileInfo.getId());
                savedFile = fileInfoService.updateFileById(fileInfo);
            }

            // 生成视频封面(如果是视频文件)
            generateVideoCover(file, filename, savedFile);
            
            // 记录耗时
            long duration = System.currentTimeMillis() - startTime;
            log.info("文件合并完成 - ID:{}, 耗时:{}ms ({}秒)", 
                    savedFile.getId(), duration, TimeUnit.MILLISECONDS.toSeconds(duration));
            
            return R.ok("文件合并成功").put("data", savedFile);
            
        } catch (Exception e) {
            log.error("文件合并失败 - 文件名:{}, 标识符:{}", filename, identifier, e);
            return R.error("文件合并失败: " + e.getMessage());
        }
    }
    
    /**
     * 构建文件信息对象
     */
    private FileInfoEntity buildFileInfo(String filename, String identifier, 
                                         Long totalSize, String type, String audioDuration) {
        FileInfoEntity fileInfo = new FileInfoEntity();
        fileInfo.setFilename(filename);
        fileInfo.setIdentifier(identifier);
        fileInfo.setTotalSize(totalSize);
        fileInfo.setType(type);
        fileInfo.setAudioDuration(audioDuration);
        return fileInfo;
    }
    
    /**
     * 构建分片文件名列表
     */
    private List<String> buildChunkNames(List<ChunkEntity> chunkEntities, 
                                         String identifier, String filename) {
        List<String> chunkNames = new ArrayList<>(chunkEntities.size());
        for (int i = 1; i <= chunkEntities.size(); i++) {
            chunkNames.add(String.format("%s/%d-%s", identifier, i, filename));
        }
        return chunkNames;
    }
    
    /**
     * 生成视频封面
     */
    private void generateVideoCover(MultipartFile file, String filename, FileInfoEntity fileInfo) {
        try {
            List<String> coverUrls = FileUtil.coverImage(file, COVER_IMAGE_COUNT, filename);
            if (!CollectionUtils.isEmpty(coverUrls)) {
                fileInfo.setCovers(coverUrls);
                log.info("视频封面生成成功 - 文件:{}, 封面数:{}", filename, coverUrls.size());
            }
        } catch (Exception e) {
            log.warn("视频封面生成失败 - 文件:{}", filename, e);
            // 封面生成失败不影响主流程,仅记录警告日志
        }
    }



//********************Minio文件服务器******************************
    /**
     * 批量删除文件
     * 根据外挂信息ID删除关联的所有文件
     */
    @Operation(summary = "批量删除文件", description = "根据外挂信息ID删除所有关联的文件")
    @PostMapping("/deleAllIn")
    public R deleteAllByInfoId(@Parameter(description = "外挂信息ID") @RequestParam Long infoId) {
        if (infoId == null || infoId <= 0) {
            return R.error("无效的外挂信息ID");
        }
        try {
            fileInfoService.deleAllIn(infoId);
            log.info("批量删除文件成功 - 外挂信息ID:{}", infoId);
            return R.ok("删除成功");
        } catch (Exception e) {
            log.error("批量删除文件失败 - 外挂信息ID:{}", infoId, e);
            return R.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件分片
     * 根据文件标识符删除所有相关分片
     */
    @Operation(summary = "删除文件分片", description = "根据文件标识符删除所有相关的分片数据")
    @PostMapping("/deleteFileChunk")
    public R deleteFileChunk(@RequestBody ChunkEntity chunk) {
        if (chunk == null || !StringUtils.hasText(chunk.getIdentifier())) {
            return R.error("文件标识符不能为空");
        }
        try {
            chunkService.deleteByChunkIdentifier(chunk.getIdentifier());
            log.info("删除文件分片成功 - 标识符:{}", chunk.getIdentifier());
            return R.ok("分片删除成功");
        } catch (Exception e) {
            log.error("删除文件分片失败 - 标识符:{}", chunk.getIdentifier(), e);
            return R.error("分片删除失败: " + e.getMessage());
        }
    }

    /**
     * 更新文件信息
     */
    @Operation(summary = "更新文件信息", description = "更新文件的关联信息")
    @PostMapping("/updateFileInfo")
    public R updateFileInfo(@RequestBody FileInfoVO infoVO) {
        if (infoVO == null || CollectionUtils.isEmpty(infoVO.getIdentifiers())) {
            return R.error("请先上传文件");
        }
        try {
            fileInfoService.updateFileInfo(infoVO.getId(), "", infoVO.getIdentifiers());
            log.info("更新文件信息成功 - ID:{}", infoVO.getId());
            return R.ok("更新成功");
        } catch (Exception e) {
            log.error("更新文件信息失败 - ID:{}", infoVO.getId(), e);
            return R.error("更新失败: " + e.getMessage());
        }
    }
    /**
     * 前端上传视频后更新文件信息
     * 
     * @author txy
     * @date 2021/12/24
     */
    @Operation(summary = "前端更新文件信息", description = "前端上传视频后更新文件关联信息和封面")
    @PostMapping("/updateFileInfoByWeb")
    public R updateFileInfoByWeb(@RequestBody FileInfoEntity entity) {
        if (entity == null || !StringUtils.hasText(entity.getIdentifier())) {
            return R.error("文件标识符不能为空");
        }
        try {
            List<String> identifiers = new ArrayList<>();
            identifiers.add(entity.getIdentifier());
            fileInfoService.updateFileInfo(entity.getWaiguaInfoId(), entity.getCover(), identifiers);
            log.info("前端更新文件信息成功 - 外挂信息ID:{}, 标识符:{}", 
                    entity.getWaiguaInfoId(), entity.getIdentifier());
            return R.ok("更新成功");
        } catch (Exception e) {
            log.error("前端更新文件信息失败 - 标识符:{}", entity.getIdentifier(), e);
            return R.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 上传图片
     * 上传单个图片并返回预览地址
     */
    @Operation(summary = "上传图片", description = "上传单个图片文件,返回预览访问地址")
    @PostMapping("/uploadImage")
    public R uploadImage(@Parameter(description = "图片文件") @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return R.error("请选择要上传的图片");
        }
        
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        log.info("上传图片 - 文件名:{}, 类型:{}, 大小:{}字节", 
                originalFilename, contentType, file.getSize());
        
        try {
            // 上传图片到MinIO
            String objectName = "images/" + originalFilename;
            MinIoUtils.uploadFileMinIo(file, objectName);
            
            // 生成预览URL
            String previewUrl = MinIoUtils.getObjectPreviewUrl(
                    BUCKET_NAME, objectName, 60 * 24 * URL_EXPIRY_DAYS, contentType);
            
            log.info("图片上传成功 - 文件名:{}", originalFilename);
            return R.ok("上传成功").put("data", previewUrl);
            
        } catch (Exception e) {
            log.error("图片上传失败 - 文件名:{}", originalFilename, e);
            return R.error("上传失败: " + e.getMessage());
        }
    }
}
