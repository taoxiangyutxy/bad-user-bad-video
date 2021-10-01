package com.ttt.one.search.service;

import com.ttt.one.common.to.es.WaiguaEsModel;

import java.io.IOException;

public interface WaiGuaSearchService {
    /**
     * 存入ES
     * @param esModel
     */
    boolean waiguaInfoSaveEs(WaiguaEsModel esModel) throws IOException;
}
