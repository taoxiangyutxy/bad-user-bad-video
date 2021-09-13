package com.ttt.one.fileServer.task;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component // 独立的组件
@Data // 生成相关的类结构
public class DynmaicCronExpression { // 动态的CRON表达式
    // 默认设置为  每天的凌晨1点执行删除操作
    private String cron = "0 10 1 * * ?"; // 每天1点10分执行           可以通过数据库的方式取数据  */5 * * * * ?
}
