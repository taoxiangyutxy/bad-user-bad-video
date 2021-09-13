package com.ttt.one.fileServer.task;

import com.ttt.one.fileServer.service.ChunkService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component //组件
@Slf4j //日志
public class ChunkScheduleTask {
    @Autowired
    private ChunkService chunkService;
    // @SchedulerLock注解里面对于任务独占锁的时间有两个配置项：
    //lockAtLeastFor : 成功执行定时任务时任务节点所能拥有独占锁的最短时间。
    //lockAtMostFor : 成功执行定时任务时任务节点所能拥有独占锁的最长时间。
  //  @Scheduled(cron = "*/2 * * * * ?") // 每2秒出发一次任务
    @SchedulerLock(name = "chunk-task",lockAtLeastFor = "2000") //2秒后开启其他任务
    public void task(){ //CRON任务
        log.info("【CRON任务】{}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        try {
            //要执行的 定时任务 删除无用的分片数据
            chunkService.deleAllByTask();
            //TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
