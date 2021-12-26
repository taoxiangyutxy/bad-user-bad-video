package com.ttt.one.fileServer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * 分片表
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-13 17:41:45
 */
@Data
@TableName("waigua_chunk")
public class ChunkEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 
	 */
	private Integer chunkNumber;
	/**
	 * 
	 */
	private Long chunkSize;
	/**
	 * 
	 */
	private Long currentChunkSize;
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
	private String relativePath;
	/**
	 * 
	 */
	private Integer totalChunks;
	/**
	 * 
	 */
	private Long totalSize;
	/**
	 * 
	 */
	private String type;
	//忽略该字段
	@TableField(exist = false)
	private Long fileInfoId;
	//忽略该字段
	@TableField(exist = false)
	private MultipartFile file;

	//忽略该字段
	@TableField(exist = false)
	private boolean status = false;

	//忽略该字段
	@TableField(exist = false)
	private List<Integer> uploaded;

}
