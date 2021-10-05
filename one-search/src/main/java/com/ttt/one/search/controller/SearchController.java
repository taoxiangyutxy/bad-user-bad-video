package com.ttt.one.search.controller;

import com.ttt.one.search.service.WaiGuaSearchService;
import com.ttt.one.search.vo.SearchParam;
import com.ttt.one.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SearchController {
    @Autowired
    private WaiGuaSearchService waiGuaSearchService;

    @GetMapping("/list.html")
    public String list(SearchParam searchParam, Model model){
        SearchResult result = waiGuaSearchService.search(searchParam);
        model.addAttribute("lists",result);
        return "list";
    }
}
