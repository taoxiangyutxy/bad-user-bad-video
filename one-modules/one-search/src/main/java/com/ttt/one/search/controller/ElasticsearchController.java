package com.ttt.one.search.controller;

import com.ttt.one.common.to.es.OperationLogInfo;
import com.ttt.one.common.to.es.WaiguaEsModel;
import com.ttt.one.common.utils.R;
import com.ttt.one.search.service.WaiGuaSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ES服务")
@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class ElasticsearchController {

    private final WaiGuaSearchService waiGuaSearchService;
    /**
     * 外挂信息单条插入ES
     *
     * @param esModel 外挂ES模型
     * @return R
     */
    @Operation(summary = "外挂信息单条插入ES")
    @PostMapping("/waiguaInfoSaveES")
    public R waiguaInfoSaveES(@RequestBody WaiguaEsModel esModel) {
        try {
            boolean success = waiGuaSearchService.waiguaInfoSaveEs(esModel);
            return !success ? R.ok() : R.error("数据存入ES失败");
        } catch (Exception e) {
            log.error("数据存入ES异常", e);
            return R.error("数据存入ES异常: " + e.getMessage());
        }
    }

    /**
     * 批量更新ES库里的链接(每6天更新一次)
     *
     * @param esModelList 外挂ES模型列表
     * @return R
     */
    @Operation(summary = "批量更新ES库里的链接")
    @PostMapping("/waiguaInfoBatchUpdate")
    public R waiguaInfoBatchUpdate(@RequestBody(required = false) List<WaiguaEsModel> esModelList) {
        try {
            waiGuaSearchService.waiguaInfoBatchUpdate(esModelList);
            return R.ok();
        } catch (Exception e) {
            log.error("批量更新ES数据异常", e);
            return R.error("批量更新ES数据异常: " + e.getMessage());
        }
    }

    /**
     * 操作日志单条插入ES
     *
     * @param logInfo 操作日志信息
     * @return R
     */
    @Operation(summary = "操作日志单条插入ES")
    @PostMapping("/operationLogSaveES")
    public R operationLogSaveES(@RequestBody OperationLogInfo logInfo) {
        log.info("操作日志信息: {}", logInfo);
        try {
            boolean success = waiGuaSearchService.operationLogSaveES(logInfo);
            return success ? R.ok() : R.error("日志存入ES失败");
        } catch (Exception e) {
            log.error("日志存入ES异常", e);
            return R.error("日志存入ES异常: " + e.getMessage());
        }
    }
}
