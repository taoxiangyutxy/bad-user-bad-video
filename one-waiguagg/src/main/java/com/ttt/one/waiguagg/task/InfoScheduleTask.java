package com.ttt.one.waiguagg.task;

import com.ttt.one.waiguagg.service.InfoService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component //组件
@Slf4j //日志
public class InfoScheduleTask {
    @Autowired
    private InfoService infoService;
    // @SchedulerLock注解里面对于任务独占锁的时间有两个配置项：
    //lockAtLeastFor : 成功执行定时任务时任务节点所能拥有独占锁的最短时间。
    //lockAtMostFor : 成功执行定时任务时任务节点所能拥有独占锁的最长时间。
    @Scheduled(cron = "0 0/5 * * * ? ")//每2秒出发一次任务
    @SchedulerLock(name = "redisToMysql-task",lockAtLeastFor = "2000") //2秒后开启其他任务
    public void task(){ //CRON任务
        log.info("【CRON任务】{}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        try {
           // TimeUnit.SECONDS.sleep(5);
            log.info("time:{}，开始执行Redis数据持久化到MySQL任务", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
            infoService.redisDataToMysql();
            log.info("time:{}，结束执行Redis数据持久化到MySQL任务", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
