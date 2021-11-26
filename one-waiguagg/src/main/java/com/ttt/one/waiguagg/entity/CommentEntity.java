package com.ttt.one.waiguagg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.ttt.one.common.vo.UserEntity;
import lombok.Data;

/**
 * 外挂评论表
 * 
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-11-24 16:23:16
 */
@Data
@TableName("waigua_comment")
public class CommentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 评论者昵称
	 */
	private String nickname;
	/**
	 * 评论头像
	 */
	private String avatar;
	/**
	 * 评论的内容
	 */
	private String content;
	/**
	 * 评论的外挂信息id
	 */
	private Long infoId;
	/**
	 * 父级评论id
	 */
	private Long parentId;
	/**
	 * 评论者id
	 */
	private Long userId;
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
	/**
	 * 点赞总数量
	 */
	private Integer thumbUpNumber;
	/**
	 * 踩 总数量
	 */
	private Integer onNumberOf;
	/**
	 * 评论总数量  可能用不到
	 */
	private Integer commentNumber;
	/**
	 * 是否置顶  0否 1是
	 */
	private Integer placedTheTop;
	/**
	 * 评论的用户等级
	 */
	@TableField(exist = false)
	private Long userLevelId;
	/**
	 * 用户名称
	 */
	@TableField(exist = false)
	private String username;
	/**
	 * 用户头像
	 */
	@TableField(exist = false)
	private String header;
	@TableField(exist = false)
	private List<CommentEntity> children;
}
