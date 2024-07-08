package com.ttt.one.common.to.es;

import lombok.Data;

import java.util.Date;

/**
 * ES  waigua数据模型
 */
@Data
public class WaiguaEsModel {

    private Long  InfoId;

    private String waiguaType;

    private String waiguaDescribe;

    private Date createTime;

    private String location;

    private String waiguaUsername;

}
