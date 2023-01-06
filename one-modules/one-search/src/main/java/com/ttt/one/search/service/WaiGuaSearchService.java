package com.ttt.one.search.service;

import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.search.vo.SearchParam;
import com.ttt.one.search.vo.SearchResult;

import java.io.IOException;
import java.util.List;

public interface WaiGuaSearchService {
    /**
     * 存入ES
     * @param esModel
     */
    boolean waiguaInfoSaveEs(WaiguaEsModel esModel) throws IOException;

    /**
     * 前台全文检索
     * @param param
     * @return
     */
     SearchResult search(SearchParam param);
    /**
     *  描述: 更新ES库里的数据  根据条件
     * @param esModelList:
     * @return void
     * @author txy
     * @description
     * @date 2021/11/19 16:58
     */
    void waiguaInfoBatchUpdate(List<WaiguaEsModel> esModelList);
}
