package com.ttt.one.waiguagg.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 一个外挂账号，会有多个举报信息,直到被永封该账号不会再接受新的举报信息。
 * 
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-08-09 10:17:14
 */
@Data
@TableName("waigua_info")
public class InfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 外挂可恨级别 1 只是透视 2 无后座 3 自瞄 4 锁头 5 穿墙
	 */
	private String waiguaType;
	/**
	 * 举报信息描述
	 */
	private String waiguaDescribe;
	/**
	 * 外挂账号id
	 */
	private Long waiguaId;
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
	 * 外挂账号名字
	 */
	private String waiguaUsername;
	/**
	 * 点赞总数
	 */
	private Integer thumbUpNumber;
	/**
	 * 阅读总数
	 */
	private Integer readNumber;
}
