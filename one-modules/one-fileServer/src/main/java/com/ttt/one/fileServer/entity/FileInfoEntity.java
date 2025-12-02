package com.ttt.one.fileServer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * 文件信息表
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-13 17:41:45
 */
@Data
@ToString
@TableName("waigua_file_info")
@Schema(description = "文件信息实体")
public class FileInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 文件ID
	 */
	@TableId
	@Schema(description = "文件唯一标识ID")
	private Long id;

	/**
	 * 文件名
	 */
	@Schema(description = "文件名称")
	private String filename;

	/**
	 * 文件标识符
	 */
	@Schema(description = "文件唯一标识符，用于文件去重")
	private String identifier;

	/**
	 * 文件存储位置
	 */
	@Schema(description = "文件存储路径位置")
	private String location;

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

	/**
	 * 关联信息ID
	 */
	@Schema(description = "关联的外部信息ID")
	private Long waiguaInfoId;

	@Schema(description = "文件创建时间")
	private Date createTime;

	@Schema(description = "音频时长(秒)")
	private String audioDuration;

	@Schema(description = "封面图片URL")
	private String cover;

	/**
	 * 封面图片url集合
	 */
	//忽略该字段
	@TableField(exist = false)
	@Schema(description = "封面图片URL集合")
	private List<String> covers;
}
