package com.ttt.one.search.controller;

import com.ttt.one.search.service.WaiGuaSearchService;
import com.ttt.one.search.vo.LogSearchParam;
import com.ttt.one.search.vo.LogSearchResult;
import com.ttt.one.search.vo.SearchParam;
import com.ttt.one.search.vo.SearchResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Tag(name = "搜索服务")
@Controller
@RequiredArgsConstructor
public class SearchController {

    private final WaiGuaSearchService waiGuaSearchService;
    /**
     * 外挂信息搜索列表页面
     *
     * @param searchParam 搜索参数
     * @param model       模型
     * @return 列表页面
     */
    @Operation(summary = "搜索列表")
    @GetMapping("/list.html")
    public String list(SearchParam searchParam, Model model) {
        SearchResult result = waiGuaSearchService.search(searchParam);
        model.addAttribute("lists", result);
        model.addAttribute("keyword", searchParam.getKeyword());
        return "list";
    }
    /**
     * 日志列表页面
     *
     * @param logSearchParam 日志搜索参数
     * @param model          模型
     * @return 日志列表页面
     */
    @Operation(summary = "日志列表")
    @GetMapping("/logList.html")
    public String logList(LogSearchParam logSearchParam, Model model) {
        //    LogSearchResult result = waiGuaSearchService.searchLog(logSearchParam);
        //     model.addAttribute("lists",result);
        return "logList";
    }
    /**
     * 日志搜索接口
     *
     * @param logSearchParam 日志搜索参数
     * @return 搜索结果
     */
    @Operation(summary = "日志搜索")
    @GetMapping("/logSearch")
    @ResponseBody
    public LogSearchResult logSearch(LogSearchParam logSearchParam) {
        return waiGuaSearchService.searchLog(logSearchParam);
    }
}
