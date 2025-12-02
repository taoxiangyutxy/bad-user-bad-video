package com.ttt.one.fileServer.task;

import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.R;
import com.ttt.one.fileServer.entity.FileInfoEntity;
import com.ttt.one.fileServer.fegin.EsSearchFeginServer;
import com.ttt.one.fileServer.service.ChunkService;
import com.ttt.one.fileServer.service.FileInfoService;
import com.ttt.one.fileServer.utils.MinIoUtils;
import com.ttt.one.fileServer.utils.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分片文件定时任务处理类
 * <p>负责定期清理无用分片数据和刷新视频访问链接</p>
 *
 * @author ttt
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChunkScheduleTask {
    
    private final ChunkService chunkService;
    private final FileInfoService fileInfoService;
    private final EsSearchFeginServer esSearchFeginServer;
    
    /**
     * MinIO链接有效期：7天（分钟数）
     */
    private static final int MINIO_URL_EXPIRY_MINUTES = 60 * 24 * 7;
    
    /**
     * 存储桶名称
     */
    private static final String BUCKET_NAME = "uploadtest";
    
    /**
     * 图片目录前缀
     */
    private static final String IMAGE_PREFIX = "images/";
    
    /**
     * 时间格式化器
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    /**
     * 定时清理无用的分片数据
     * <p>每6天凌晨0点执行一次，清理系统中未合并的无效分片文件</p>
     * <p>使用ShedLock确保分布式环境下任务只执行一次</p>
     *
     * @SchedulerLock注解说明：
     * - lockAtLeastFor：任务执行后锁的最小持有时间
     * - lockAtMostFor：锁的最大持有时间（防止任务异常导致锁无法释放）
     */
    @Scheduled(cron = "0 0 0 1/6 * ?")
    @SchedulerLock(name = "chunk-cleanup-task", lockAtLeastFor = "PT2S", lockAtMostFor = "PT10M")
    public void cleanupUnusedChunks() {
        String currentTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        log.info("[定时任务-清理无用分片] 开始执行，执行时间: {}", currentTime);
        
        try {
            chunkService.deleAllByTask();
            log.info("[定时任务-清理无用分片] 执行成功");
        } catch (Exception e) {
            log.error("[定时任务-清理无用分片] 执行失败", e);
        }
    }
    /**
     * 定时刷新视频访问链接
     * <p>每5分钟执行一次，刷新已审核通过且链接已过期的视频访问URL</p>
     * <p>MinIO预签名URL有效期为7天，需要定期刷新以保证前端可访问</p>
     * <p>同时更新MySQL数据库和Elasticsearch索引中的链接</p>
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    @SchedulerLock(name = "video-url-refresh-task", lockAtLeastFor = "PT2S", lockAtMostFor = "PT30M")
    public void refreshVideoAccessUrls() {
        String currentTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        log.info("[定时任务-刷新视频链接] 开始执行，执行时间: {}", currentTime);
        
        try {
            // 获取审核通过且链接已过期的视频列表
            List<FileInfoEntity> expiredVideoList = fileInfoService.listByPassAndExpired();
            
            if (CollectionUtils.isEmpty(expiredVideoList)) {
                log.info("[定时任务-刷新视频链接] 无需刷新的视频，任务结束");
                return;
            }
            
            log.info("[定时任务-刷新视频链接] 找到{}个需要刷新的视频", expiredVideoList.size());
            
            // 批量更新视频和封面图片的访问链接
            List<FileInfoEntity> updatedVideoList = refreshVideoAndCoverUrls(expiredVideoList);
            
            // 批量更新数据库
            fileInfoService.updateBatchById(updatedVideoList);
            log.info("[定时任务-刷新视频链接] 数据库更新成功");
            
            // 同步更新Elasticsearch索引
            updateElasticsearchIndex(updatedVideoList);
            
            log.info("[定时任务-刷新视频链接] 执行成功，共刷新{}个视频", updatedVideoList.size());
            
        } catch (Exception e) {
            log.error("[定时任务-刷新视频链接] 执行失败", e);
        }
    }
    
    /**
     * 刷新视频和封面图片的访问URL
     *
     * @param videoList 需要刷新的视频列表
     * @return 更新后的视频列表
     */
    private List<FileInfoEntity> refreshVideoAndCoverUrls(List<FileInfoEntity> videoList) {
        return videoList.stream()
                .map(this::refreshSingleVideoUrls)
                .collect(Collectors.toList());
    }
    
    /**
     * 刷新单个视频的访问URL（包括视频和封面）
     *
     * @param video 视频实体
     * @return 更新后的视频实体
     */
    private FileInfoEntity refreshSingleVideoUrls(FileInfoEntity video) {
        // 生成视频访问链接
        String videoPath = video.getIdentifier() + "/" + video.getFilename();
        String videoUrl = MinIoUtils.getObjectUrl(BUCKET_NAME, videoPath, MINIO_URL_EXPIRY_MINUTES);
        video.setLocation(videoUrl);
        video.setCreateTime(new Date());
        
        // 生成封面图片访问链接
        String coverFileName = StrUtil.extractFileName(video.getCover());
        String coverPath = IMAGE_PREFIX + coverFileName;
        String coverUrl = MinIoUtils.getObjectPreviewUrl(BUCKET_NAME, coverPath, MINIO_URL_EXPIRY_MINUTES, "image/jpeg");
        video.setCover(coverUrl);
        
        return video;
    }
    
    /**
     * 更新Elasticsearch索引中的视频链接信息
     *
     * @param videoList 已更新的视频列表
     */
    private void updateElasticsearchIndex(List<FileInfoEntity> videoList) {
        List<WaiguaEsModel> esModelList = videoList.stream()
                .map(this::convertToEsModel)
                .collect(Collectors.toList());
        
        try {
            R response = esSearchFeginServer.waiguaInfoBatchUpdate(esModelList);
            if (response != null && response.getCode() == 0) {
                log.info("[定时任务-刷新视频链接] Elasticsearch索引更新成功");
            } else {
                log.warn("[定时任务-刷新视频链接] Elasticsearch索引更新返回异常: {}", response);
            }
        } catch (Exception e) {
            log.error("[定时任务-刷新视频链接] Elasticsearch索引更新失败，远程调用异常", e);
        }
    }
    
    /**
     * 将FileInfoEntity转换为WaiguaEsModel
     *
     * @param fileInfo 文件信息实体
     * @return ES模型对象
     */
    private WaiguaEsModel convertToEsModel(FileInfoEntity fileInfo) {
        WaiguaEsModel esModel = new WaiguaEsModel();
        esModel.setInfoId(fileInfo.getWaiguaInfoId());
        esModel.setLocation(fileInfo.getLocation());
        esModel.setCreateTime(fileInfo.getCreateTime());
        return esModel;
    }
}
