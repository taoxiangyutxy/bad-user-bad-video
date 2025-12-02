package com.ttt.one.fileServer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * 分片表
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-13 17:41:45
 */
@Data
@TableName("waigua_chunk")
@Schema(description = "文件分片信息实体")
public class ChunkEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分片ID
	 */
	@TableId
	@Schema(description = "分片唯一标识ID")
	private Long id;

	/**
	 * 分片序号
	 */
	@Schema(description = "当前分片的序号(从1开始)")
	private Integer chunkNumber;

	/**
	 * 分片大小
	 */
	@Schema(description = "分片的标准大小(字节)")
	private Long chunkSize;

	/**
	 * 当前分片实际大小
	 */
	@Schema(description = "当前分片的实际大小(字节)")
	private Long currentChunkSize;

	/**
	 * 文件名
	 */
	@Schema(description = "原始文件名称")
	private String filename;

	/**
	 * 文件标识符
	 */
	@Schema(description = "文件唯一标识符，用于标识同一文件的不同分片")
	private String identifier;

	/**
	 * 相对路径
	 */
	@Schema(description = "文件相对路径")
	private String relativePath;

	/**
	 * 总分片数
	 */
	@Schema(description = "文件总共的分片数量")
	private Integer totalChunks;

	/**
	 * 文件总大小
	 */
	@Schema(description = "文件总大小(字节)")
	private Long totalSize;

	/**
	 * 文件类型
	 */
	@Schema(description = "文件MIME类型")
	private String type;

	//忽略该字段
	@TableField(exist = false)
	@Schema(description = "关联的文件信息ID")
	private Long fileInfoId;

	//忽略该字段
	@TableField(exist = false)
	@Schema(description = "上传的文件分片数据")
	private MultipartFile file;

	//忽略该字段
	@TableField(exist = false)
	@Schema(description = "分片上传状态")
	private boolean status = false;

	//忽略该字段
	@TableField(exist = false)
	@Schema(description = "已上传的分片序号列表")
	private List<Integer> uploaded;
}
