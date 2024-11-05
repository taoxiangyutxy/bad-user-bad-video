package com.ttt.one.common.to.es;

import lombok.Data;

import java.util.Date;

/**
 * ES  日志数据模型
 */
@Data
public class OperationLogInfo {

    private Integer id;
    /**
     * 表名（不映射数据库字段）
     */
    private String tableName;
    /**
     * 应用名称
     */
    private String applicationName;
    /**
     * 应用描述
     */
    private String applicationDesc;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * URL
     */
    private String url;
    /**
     * uri
     */
    private String uri;
    /**
     * 客户端IP
     */
    private String ip;
    /**
     * 接口调用方式
     */
    private String method;
    /**
     * 参数
     */
    private String param;
    /**
     * 操作类型码
     */
    private String opTypeCode;
    /**
     * 操作类型名称
     */
    private String opTypeName;
    /**
     * 接口描述
     */
    private String opDesc;
    /**
     * 接口调用时间
     */
    private Date opTime;
    /**
     * 接口响应时间
     */
    private Integer consumeTime;
    /**
     * 业务成功状态
     */
    private String success;
    /**
     * 业务错误原因
     */
    private String error;
    /**
     * 业务返回数据
     */
    private String result;
    /**
     * 业务类型码
     */
    private String dtype;
    /**
     * 用户登录名
     */
    private String userName;
    /**
     * 用户姓名
     */
    private String realName;
    /**
     * 企业全称
     */
    private String memberName;
    /**
     * 附加字段1
     */
    private String extend1;
    /**
     * 附加字段2
     */
    private String extend2;
    /**
     * 附加字段3
     */
    private String extend3;
    /**
     * 调用时间转换
     */
    private String dateStr;
}
