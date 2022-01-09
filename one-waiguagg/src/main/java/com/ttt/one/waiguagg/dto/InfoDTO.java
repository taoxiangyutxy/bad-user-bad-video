package com.ttt.one.waiguagg.dto;

import com.ttt.one.waiguagg.entity.InfoEntity;
import lombok.Data;

/**
 * 信息表 DTO 如果有外来参数 可以放这里  相当于wrapper了
 */
@Data
public class InfoDTO extends InfoEntity {
    private String key;
    private Integer pageIndex;
    private Integer pageSize;
}
