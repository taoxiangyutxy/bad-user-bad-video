package com.ttt.one.search.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LogSearchParam {
    @Schema(description = "全文匹配关键字")
    private String keyword; //全文匹配关键字
    @Schema(description = "排序条件")
    private String sort;  //排序条件   time_asc/desc
    /**
     * 表名   唯一值过滤
     */
    @Schema(description = "表名")
    private String tableName;

    /**
     * 方法名 唯一值过滤
     */
    @Schema(description = "方法名")
    private String methodName;
    /**
     * 参数  模糊查询
     */
    @Schema(description = "参数")
    private String param;
    /**
     * 业务错误原因  模糊查询
     */
    @Schema(description = "业务错误原因")
    private String error;
    /**
     * 业务返回数据   模糊查询
     */
    @Schema(description = "业务返回数据")
    private String result;
    /**
     * 用户登录名    模糊查询
     */
    @Schema(description = "用户登录名")
    private String userName;
    /**
     * 用户姓名     模糊查询
     */
    @Schema(description = "用户姓名")
    private String realName;
    /**
     * 企业全称     模糊查询
     */
    @Schema(description = "企业全称")
    private String memberName;
    /**
     * 接口调用时间    时间段查询
     */
    @Schema(description = "接口调用时间")
    private Date opTime;
    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    private Date startTime;
    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private Date endTime;
    @Schema(description = "聚合分析类型")
    private Integer pageNum = 1; // 页码
    @Schema(description = "页数")
    private Integer size =10;

}
