package com.ttt.one.fileServer.config;

import com.ttt.one.fileServer.task.ChunkScheduleTask;
import com.ttt.one.fileServer.task.DynmaicCronExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

/**
 * CRON表达式的动态配置类
 * <p>用于动态配置定时任务的执行时间</p>
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DynmaicScheduleConfig implements SchedulingConfigurer {
    
    private final DynmaicCronExpression expression;
    private final ChunkScheduleTask chunkScheduleTask;
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 动态配置分片清理任务
        taskRegistrar.addTriggerTask(
                chunkScheduleTask::cleanupUnusedChunks,
                triggerContext -> {
                    String cron = expression.getCron();
                    log.info("设置当前的CRON表达式：{}", cron);
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }
        );
    }
}
