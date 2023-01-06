package com.ttt.one.waiguagg.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 接收远程服务 返回的视频信息表
 */
@Data
public class FileInfoVO {
    /**
     *
     */
    @TableId
    private Long id;
    /**
     *
     */
    private String filename;
    /**
     *
     */
    private String identifier;
    /**
     *
     */
    private String location;
    /**
     *
     */
    private Long totalSize;
    /**
     *
     */
    private String type;
    /**
     *
     */
    private Long waiguaInfoId;

    private Date createTime;

    private String cover;
}
