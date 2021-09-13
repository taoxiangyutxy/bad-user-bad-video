package com.ttt.one.waiguagg.vo;

import lombok.Data;

import java.util.Date;

/**
 * 视频预览返回前端  vo
 */
@Data
public class VideoPreviewVO {
    /**
     * 举报信息描述
     */
    private String waiguaDescribe;
    /**
     * 视频上传时间
     */
    private Date createTime;

    /**
     * 拼接的  预览视频路径  /static/video/ttt.mp4
     */
    private String movie;
}
