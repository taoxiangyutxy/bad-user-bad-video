package com.ttt.one.search.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SearchParam {
    @Schema(description = "搜索关键字")
    private String keyword; //全文匹配关键字
    @Schema(description = "排序条件")
    private String sort;  //排序条件   time_asc/desc
    /**
     * 分类条件过滤  or
     */
    @Schema(description = "分类条件")
    private String waiguaType;
    @Schema(description = "用户名")
    private String waiguaUsername;
    @Schema(description = "页码")
    private Integer pageNum = 1; // 页码
    @Schema(description = "聚合分析类型")
    private List<String> waiguaTypes; //聚合分析类型
}
