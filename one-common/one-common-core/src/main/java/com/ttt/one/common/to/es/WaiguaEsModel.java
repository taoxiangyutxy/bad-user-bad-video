package com.ttt.one.common.to.es;

import lombok.Data;

import java.util.Date;

/**
 * ES  waigua数据模型
 */
@Data
public class WaiguaEsModel {
    /*
    *
    * InfoId" : {
          "type" : "long"
        },
        "waiguaType" : {
          "type" : "text"
        },
        "waiguaDescribe" : {
          "type" : "keyword"
        },
        "createTime" : {
          "type" : "keyword"
        },
        "location" : {
          "type" : "text",
          "index" : false,
          "doc_values" : false
        }
    *
    * */
    private Long  InfoId;

    private String waiguaType;

    private String waiguaDescribe;

    private Date createTime;

    private String location;

    private String waiguaUsername;

}
