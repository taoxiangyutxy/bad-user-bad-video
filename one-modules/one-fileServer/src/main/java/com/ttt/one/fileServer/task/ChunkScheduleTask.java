package com.ttt.one.fileServer.task;

import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.R;
import com.ttt.one.fileServer.entity.FileInfoEntity;
import com.ttt.one.fileServer.fegin.EsSearchFeginServer;
import com.ttt.one.fileServer.service.ChunkService;
import com.ttt.one.fileServer.service.FileInfoService;
import com.ttt.one.fileServer.utils.MinIoUtils;
import com.ttt.one.fileServer.utils.StrUtil;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component //组件
@Slf4j //日志
public class ChunkScheduleTask {
    @Autowired
    private ChunkService chunkService;
    @Autowired
    private  FileInfoService fileInfoService;
    @Autowired
    private EsSearchFeginServer esSearchFeginServer;
    // @SchedulerLock注解里面对于任务独占锁的时间有两个配置项：
    //lockAtLeastFor : 成功执行定时任务时任务节点所能拥有独占锁的最短时间。
    //lockAtMostFor : 成功执行定时任务时任务节点所能拥有独占锁的最长时间。
    @Scheduled(cron = "0 0 0 1/6 * ?") // 每天凌晨1点出发一次任务
    @SchedulerLock(name = "chunk-task",lockAtLeastFor = "2000") //2秒后开启其他任务
    public void task(){ //CRON任务
        log.info("【CRON任务:清空无用的分片数据】{}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        try {
            //要执行的 定时任务 删除无用的分片数据
            chunkService.deleAllByTask();
            //TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Scheduled(cron = "0 0/5 * * * ?") // 0 0 0 1/6 * ?  1号开始每6天出发一次任务   0 0/5 * * * ? 5分钟执行一次
    @SchedulerLock(name = "videoUrlDate-task",lockAtLeastFor = "2000") //2秒后开启其他任务
    public void task2(){ //CRON任务
        log.info("【CRON任务:每周刷新下视频访问链接】{}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        try {
            /**
             * 获取库里所有的视频数据  应该是审核通过前台可展示的所有视频 并且是已经到期的（Minio 7天链接失效）
             */
            List<FileInfoEntity> list = fileInfoService.listByPassAndExpired();
            list = list.stream().map(fileInfoEntity -> {
                String url = MinIoUtils.getObjectUrl("uploadtest", fileInfoEntity.getIdentifier()+"/"+fileInfoEntity.getFilename(), 60 * 24*7);
                fileInfoEntity.setCreateTime(new Date());
                fileInfoEntity.setLocation(url);

                //同时更新封面图片链接
                String previewUrl = MinIoUtils.getObjectPreviewUrl("uploadtest",
                        "images/" + StrUtil.extractFileName(fileInfoEntity.getCover()), 60 * 24 * 7, "image/jpeg");
                fileInfoEntity.setCover(previewUrl);
                return fileInfoEntity;
            }).collect(Collectors.toList());
            //循环更新sql 不要一条一条的更新
            fileInfoService.updateBatchById(list);
            /**
             * ES库里 也要更新下链接
             */
            List<WaiguaEsModel> esModelList =list.stream().map(fileInof->{
                WaiguaEsModel esModel = new WaiguaEsModel();
                esModel.setInfoId(fileInof.getWaiguaInfoId());
                esModel.setLocation(fileInof.getLocation());
                esModel.setCreateTime(fileInof.getCreateTime());
                return esModel;
            }).collect(Collectors.toList());
            R r = null;
            try {
                r = esSearchFeginServer.waiguaInfoBatchUpdate(esModelList);
                if(r.getCode()==0){
                    log.info("调用成功！");
                }
            } catch (Exception e) {
                log.error("远程调用失败！waiguaInfoBatchUpdate");
                e.printStackTrace();
            }
            //TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
