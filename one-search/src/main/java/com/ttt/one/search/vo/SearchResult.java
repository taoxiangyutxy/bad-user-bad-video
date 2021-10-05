package com.ttt.one.search.vo;

import com.ttt.one.common.to.es.WaiguaEsModel;
import lombok.Data;

import java.util.List;

/**
 * ES查询返回结果
 */
@Data
public class SearchResult {
    private List<WaiguaEsModel> esModels;
    private Integer pageNum; //页码
    private Long total; //总记录数
    private Integer totalPages; //总页码
    private List<String> waiguaTypes; //聚合分析类型
}
