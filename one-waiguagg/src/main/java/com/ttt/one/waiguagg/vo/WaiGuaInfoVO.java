package com.ttt.one.waiguagg.vo;

import lombok.Data;

import java.util.Date;

/**
 * 外挂完整信息 VO
 */
@Data
public class WaiGuaInfoVO {
    /**
     * 外挂账号主键
     */
    private Long waiguaId;
    /**
     * 外挂账号名
     */
    private String waiguaUsername;
    /**
     * 账号等级
     */
    private Integer waiguaGrade;
    /**
     * 封禁状态 0 一天 1 三天 2永封
     */
    private Integer sealState;
    /**
     * 封禁开始时间
     */
    private Date sealStartTime;
    /**
     * 封禁结束时间
     */
    private Date sealEndTime;

    /**
     * 外挂信息主键
     */
    private Long waiguaInfoId;

    /**
     * 外挂可恨级别 1 只是透视 2 无后座 3 自瞄 4 锁头 5 穿墙
     */
    private String[] waiguaType;
    /**
     * 举报信息描述
     */
    private String waiguaDescribe;
    /**
     * 举报用户id
     */
    private Long reportuserId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updataTime;
    /**
     * 状态 0 存在  1 删除
     */
    private Integer status;
    /**
     * 审核状态 0 待审核  1 审核中  2 审核通过 3 驳回
     */
    private Integer reviewStatus;
    /**
     * 举报用户名称
     */
    private String reportuserName;

    /**
     * 点赞总数
     */
    private Integer thumbUpNumber;
    /**
     * 阅读总数
     */
    private Integer readNumber;
    /**
     * 是否点赞 0否 1是
     */
    private Integer isSupport;
    /**
     * 评论数
     */
    private Integer commentConut;

    /****************视频相关**********************/
    /**
     * 下载链接
     */
    private String location;


}
