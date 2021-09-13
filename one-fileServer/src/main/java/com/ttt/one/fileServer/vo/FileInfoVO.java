package com.ttt.one.fileServer.vo;

import lombok.Data;

import java.util.List;

/**
 * 文件上传信息  参数vo
 */
@Data
public class FileInfoVO {
    private Long id;
    private List<String> identifiers;
}
