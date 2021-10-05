package com.ttt.one.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {
    private String keyword; //全文匹配关键字
    private String sort;  //排序条件   time_asc/desc
    /**
     * 分类条件过滤  or
     */
    private String waiguaType;

    private String waiguaUsername;

    private Integer pageNum = 1; // 页码

    private List<String> waiguaTypes; //聚合分析类型
}
