package com.ttt.one.search.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LogSearchParam {
    private String keyword; //全文匹配关键字
    private String sort;  //排序条件   time_asc/desc
    /**
     * 表名   唯一值过滤
     */
    private String tableName;

    /**
     * 方法名 唯一值过滤
     */
    private String methodName;
    /**
     * 参数  模糊查询
     */
    private String param;
    /**
     * 业务错误原因  模糊查询
     */
    private String error;
    /**
     * 业务返回数据   模糊查询
     */
    private String result;
    /**
     * 用户登录名    模糊查询
     */
    private String userName;
    /**
     * 用户姓名     模糊查询
     */
    private String realName;
    /**
     * 企业全称     模糊查询
     */
    private String memberName;
    /**
     * 接口调用时间    时间段查询
     */
    private Date opTime;
    /**
     * 开始日期
     */
    private Date startTime;
    /**
     * 结束日期
     */
    private Date endTime;

    private Integer pageNum = 1; // 页码

}
