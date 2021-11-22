package com.ttt.one.waiguagg.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 点赞表
 * 
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-11-22 16:21:39
 */
@Data
@TableName("waigua_giveLike")
public class GivelikeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 关联ID
	 */
	private Long relationId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 类型
	 */
	private Integer type;
	/**
	 * 删除标识 0未删除 1已删除
	 */
	private Integer delFlag;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 删除时间
	 */
	private Date deleteTime;

}
