/*
 Navicat Premium Data Transfer

 Source Server         : docker-mysql-56.10
 Source Server Type    : MySQL
 Source Server Version : 50734
 Source Host           : 192.168.56.10:3306
 Source Schema         : ttt_one_waigua

 Target Server Type    : MySQL
 Target Server Version : 50734
 File Encoding         : 65001

 Date: 12/09/2021 17:21:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for waigua_chunk
-- ----------------------------
DROP TABLE IF EXISTS `waigua_chunk`;
CREATE TABLE `waigua_chunk`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chunk_number` int(11) NOT NULL COMMENT '分片序号',
  `chunk_size` bigint(20) NOT NULL COMMENT '固定分片大小',
  `current_chunk_size` bigint(20) NOT NULL COMMENT '当前分片实际大小',
  `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名称带后缀',
  `identifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'md5文件唯一值',
  `relative_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '相对路径',
  `total_chunks` int(11) NOT NULL COMMENT '文件总分片数',
  `total_size` bigint(20) NOT NULL COMMENT '文件总大小',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 173 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for waigua_file_info
-- ----------------------------
DROP TABLE IF EXISTS `waigua_file_info`;
CREATE TABLE `waigua_file_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件名称',
  `identifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件唯一标识md5值',
  `location` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件存放路径',
  `total_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  `waigua_info_id` bigint(20) NULL DEFAULT NULL COMMENT '外挂信息表id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '视频上传时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 96 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for waigua_info
-- ----------------------------
DROP TABLE IF EXISTS `waigua_info`;
CREATE TABLE `waigua_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `waigua_type` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外挂可恨级别 1 只是透视 2 无后座 3 自瞄 4 锁头 5 穿墙',
  `waigua_describe` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '举报描述信息',
  `waigua_id` bigint(20) NULL DEFAULT NULL COMMENT '外挂账号 id',
  `reportuser_id` bigint(20) NULL DEFAULT NULL COMMENT '举报用户 id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updata_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态 0 存在  1 删除',
  `review_status` int(11) NULL DEFAULT NULL COMMENT '审核状态 0 待审核  1 审核中  2 审核通过 3 驳回',
  `waigua_username` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外挂账号名字（关键字查询）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '一个外挂账号，会有多个举报信息,直到被永封该账号不会再接受新的举报信息。' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for waigua_unmber
-- ----------------------------
DROP TABLE IF EXISTS `waigua_unmber`;
CREATE TABLE `waigua_unmber`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `waigua_username` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '使用外挂账号名',
  `waigua_grade` int(11) NULL DEFAULT NULL COMMENT '账号等级',
  `hateful_type` int(11) NULL DEFAULT NULL COMMENT '外挂可恨级别 1 只是透视 2 无后座 3 自瞄 4 锁头 5 全占',
  `seal_state` int(11) NULL DEFAULT NULL COMMENT '封禁状态 1 一天 2 三天 3永封',
  `seal_start_time` datetime(0) NULL DEFAULT NULL COMMENT '封禁开始时间',
  `seal_end_time` datetime(0) NULL DEFAULT NULL COMMENT '封禁结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '外挂账号' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
