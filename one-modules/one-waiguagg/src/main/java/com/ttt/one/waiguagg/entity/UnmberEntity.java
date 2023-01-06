package com.ttt.one.waiguagg.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 外挂账号
 * 
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-09 10:17:14
 */
@Data
@TableName("waigua_unmber")
public class UnmberEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 外挂账号名
	 */
	private String waiguaUsername;
	/**
	 * 账号等级
	 */
	private Integer waiguaGrade;
	/**
	 * 可恨类型
	 */
	private Integer hatefulType;
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

}
