package com.ttt.one.search.controller;

import com.ttt.one.search.service.WaiGuaSearchService;
import com.ttt.one.search.vo.LogSearchParam;
import com.ttt.one.search.vo.LogSearchResult;
import com.ttt.one.search.vo.SearchParam;
import com.ttt.one.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SearchController {
    @Autowired
    private WaiGuaSearchService waiGuaSearchService;

    @GetMapping("/list.html")
    public String list(SearchParam searchParam, Model model){
        SearchResult result = waiGuaSearchService.search(searchParam);
        model.addAttribute("lists",result);
        model.addAttribute("keyword",searchParam.getKeyword());
        return "list";
    }

    @GetMapping("/logList.html")
    public String logList(LogSearchParam logSearchParam, Model model){
    //    LogSearchResult result = waiGuaSearchService.searchLog(logSearchParam);
   //     model.addAttribute("lists",result);
        return "logList";
    }
    @GetMapping("/logSearch")
    @ResponseBody
    private LogSearchResult logSearch(LogSearchParam logSearchParam, Model model) {
        LogSearchResult result = waiGuaSearchService.searchLog(logSearchParam);// 返回搜索结果
        return result;
    }
}
