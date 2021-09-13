package com.ttt.one.fileServer.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 文件信息表
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-13 17:41:45
 */
@Data
@TableName("waigua_file_info")
public class FileInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

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
}
