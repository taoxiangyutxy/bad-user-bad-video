

package com.ttt.one.common.utils;

/**
 * 常量
 *
 */
public class Constant {
    /**
     * 验证码前缀
     */
    public static final String SMS_CODE_CACHE_PREFIX = "sms:code:";

	/** 超级管理员ID */
	public static final int SUPER_ADMIN = 1;
    /**
     * 当前页码
     */
    public static final String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "limit";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";

    /**
     *  存在
     */
    public static final int STATUS_0 = 0;
    /**
     * 假删除
     */
    public static final int STATUS_1 = 1;


    /**
     * 待审核
     */
    public static final int REVIEWSTATUS_0 = 0;
    /**
     * 审核中
     */
    public static final int REVIEWSTATUS_1 = 1;
    /**
     * 审核通过
     */
    public static final int REVIEWSTATUS_2 = 2;
    /**
     * 驳回
     */
    public static final int REVIEWSTATUS_3 = 3;
    /**
     * 登录成功的用户 session-key
     */
    public static final String LOGIN_USER ="loginUser";
    /**
     * 点赞类型
     */
    public static final int LIKETYPE_INFO = 1;
    public static final int LIKETYPE_COMMENT = 2;

    /**
	 * 菜单类型
	 *
	 */
    public enum MenuType {
        /**
         * 目录
         */
    	CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    /**
     * 定时任务状态
     *
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
    	NORMAL(0),
        /**
         * 暂停
         */
    	PAUSE(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    /** 主子表（增删改查） */
    public static final String TPL_SUB = "sub";

    /** 树编码字段 */
    public static final String TREE_CODE = "treeCode";
    /** 单表（增删改查） */
    public static final String TPL_CRUD = "crud";
    /** Entity基类字段 */
    public static final String[] BASE_ENTITY = { "createBy", "createTime", "updateBy", "updateTime", "remark" };

    /** Tree基类字段 */
    public static final String[] TREE_ENTITY = { "parentName", "parentId", "orderNum", "ancestors" };
}
