package com.ttt.one.auth.vo;

import com.ttt.one.oplog.bean.OperationLogBase;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 日志基础表
 * </p>
 *
 * @author wangxiaolong
 * @since 2019-11-21
 */
public class OperationLogInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 表名（不映射数据库字段）
     */
    private String tableName;
    /**
     * 应用名称
     */
    private String applicationName;
    /**
     * 应用描述
     */
    private String applicationDesc;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * URL
     */
    private String url;
    /**
     * uri
     */
    private String uri;
    /**
     * 客户端IP
     */
    private String ip;
    /**
     * 接口调用方式
     */
    private String method;
    /**
     * 参数
     */
    private String param;
    /**
     * 操作类型码
     */
    private String opTypeCode;
    /**
     * 操作类型名称
     */
    private String opTypeName;
    /**
     * 接口描述
     */
    private String opDesc;
    /**
     * 接口调用时间
     */
    private Date opTime;
    /**
     * 接口响应时间
     */
    private Integer consumeTime;
    /**
     * 业务成功状态
     */
    private String success;
    /**
     * 业务错误原因
     */
    private String error;
    /**
     * 业务返回数据
     */
    private String result;
    /**
     * 业务类型码
     */
    private String dtype;
    /**
     * 用户登录名
     */
    private String userName;
    /**
     * 用户姓名
     */
    private String realName;
    /**
     * 企业全称
     */
    private String memberName;
    /**
     * 附加字段1
     */
    private String extend1;
    /**
     * 附加字段2
     */
    private String extend2;
    /**
     * 附加字段3
     */
    private String extend3;
    /**
     * 附加字段4
     */
    private String extend4;
    /**
     * 附加字段5
     */
    private String extend5;
    /**
     * 附加字段6
     */
    private String extend6;
    /**
     * 附加字段7
     */
    private String extend7;
    /**
     * 附加字段8
     */
    private String extend8;
    /**
     * 附加字段9
     */
    private String extend9;
    /**
     * 附加字段10
     */
    private String extend10;
    /**
     * 附加字段11
     */
    private String extend11;
    /**
     * 附加字段12
     */
    private String extend12;
    /**
     * 附加字段13
     */
    private String extend13;
    /**
     * 附加字段14
     */
    private String extend14;
    /**
     * 附加字段15
     */
    private String extend15;
    /**
     * 附加字段16
     */
    private String extend16;
    /**
     * 附加字段17
     */
    private String extend17;
    /**
     * 附加字段18
     */
    private String extend18;
    /**
     * 附加字段19
     */
    private String extend19;
    /**
     * 附加字段20
     */
    private String extend20;
    /**
     * 插入时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationDesc() {
        return applicationDesc;
    }

    public void setApplicationDesc(String applicationDesc) {
        this.applicationDesc = applicationDesc;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getOpTypeCode() {
        return opTypeCode;
    }

    public void setOpTypeCode(String opTypeCode) {
        this.opTypeCode = opTypeCode;
    }

    public String getOpTypeName() {
        return opTypeName;
    }

    public void setOpTypeName(String opTypeName) {
        this.opTypeName = opTypeName;
    }

    public String getOpDesc() {
        return opDesc;
    }

    public void setOpDesc(String opDesc) {
        this.opDesc = opDesc;
    }

    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }

    public Integer getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(Integer consumeTime) {
        this.consumeTime = consumeTime;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    public String getExtend3() {
        return extend3;
    }

    public void setExtend3(String extend3) {
        this.extend3 = extend3;
    }

    public String getExtend4() {
        return extend4;
    }

    public void setExtend4(String extend4) {
        this.extend4 = extend4;
    }

    public String getExtend5() {
        return extend5;
    }

    public void setExtend5(String extend5) {
        this.extend5 = extend5;
    }

    public String getExtend6() {
        return extend6;
    }

    public void setExtend6(String extend6) {
        this.extend6 = extend6;
    }

    public String getExtend7() {
        return extend7;
    }

    public void setExtend7(String extend7) {
        this.extend7 = extend7;
    }

    public String getExtend8() {
        return extend8;
    }

    public void setExtend8(String extend8) {
        this.extend8 = extend8;
    }

    public String getExtend9() {
        return extend9;
    }

    public void setExtend9(String extend9) {
        this.extend9 = extend9;
    }

    public String getExtend10() {
        return extend10;
    }

    public void setExtend10(String extend10) {
        this.extend10 = extend10;
    }

    public String getExtend11() {
        return extend11;
    }

    public void setExtend11(String extend11) {
        this.extend11 = extend11;
    }

    public String getExtend12() {
        return extend12;
    }

    public void setExtend12(String extend12) {
        this.extend12 = extend12;
    }

    public String getExtend13() {
        return extend13;
    }

    public void setExtend13(String extend13) {
        this.extend13 = extend13;
    }

    public String getExtend14() {
        return extend14;
    }

    public void setExtend14(String extend14) {
        this.extend14 = extend14;
    }

    public String getExtend15() {
        return extend15;
    }

    public void setExtend15(String extend15) {
        this.extend15 = extend15;
    }

    public String getExtend16() {
        return extend16;
    }

    public void setExtend16(String extend16) {
        this.extend16 = extend16;
    }

    public String getExtend17() {
        return extend17;
    }

    public void setExtend17(String extend17) {
        this.extend17 = extend17;
    }

    public String getExtend18() {
        return extend18;
    }

    public void setExtend18(String extend18) {
        this.extend18 = extend18;
    }

    public String getExtend19() {
        return extend19;
    }

    public void setExtend19(String extend19) {
        this.extend19 = extend19;
    }

    public String getExtend20() {
        return extend20;
    }

    public void setExtend20(String extend20) {
        this.extend20 = extend20;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
