package com.ttt.one.search.service;

import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.search.vo.SearchParam;
import com.ttt.one.search.vo.SearchResult;

import java.io.IOException;

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
}
