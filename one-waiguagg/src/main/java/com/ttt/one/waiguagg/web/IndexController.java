package com.ttt.one.waiguagg.web;

import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.waiguagg.service.InfoService;
import com.ttt.one.waiguagg.vo.WaiGuaInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    InfoService infoService;
    @GetMapping({"/","/index.html"})
    public String indexPage(@RequestParam Map<String, Object> params, Model model){
        List<WaiGuaInfoVO> lists = infoService.pageAllWaiGua(params);
        model.addAttribute("lists",lists);
        //有默认前缀  和默认后缀 所以直接index即可   视图解析器进行拼串
        return "index";
    }
}
