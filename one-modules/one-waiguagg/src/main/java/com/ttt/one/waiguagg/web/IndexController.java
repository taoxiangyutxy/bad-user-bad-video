package com.ttt.one.waiguagg.web;

import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.vo.UserEntity;
import com.ttt.one.waiguagg.entity.CommentEntity;
import com.ttt.one.waiguagg.entity.InfoEntity;
import com.ttt.one.waiguagg.service.CommentService;
import com.ttt.one.waiguagg.service.InfoService;
import com.ttt.one.waiguagg.vo.WaiGuaInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Slf4j
@Controller
public class IndexController {
    @Autowired
    InfoService infoService;
    @Autowired
    private CommentService commentService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @ResponseBody
    @RequestMapping("/test")
    public String createUserTest(){
        InfoEntity entity = new InfoEntity();
        entity.setWaiguaUsername("测试测试");
        entity.setWaiguaDescribe("MQMQMQMQMQ");
        entity.setCreateTime(new Date());
        //消息发送给  create.ttt
        rabbitTemplate.convertAndSend("topic-exchange","create.ttt",entity);
        return "ok:"+new Date();
    }


    /**
     * 首页
     * @param params
     * @param model
     * @return
     */
    @GetMapping({"/","/index.html"})
    public String indexPage(@RequestParam Map<String, Object> params, Model model){
        List<WaiGuaInfoVO> lists = infoService.pageAllWaiGua(params);
        model.addAttribute("lists",lists);
        //有默认前缀  和默认后缀 所以直接index即可   视图解析器进行拼串
        return "index";
    }
    /**
     *  描述: 单条外挂信息 展示页
     * @param id:
     * @param model:
     * @return String
     * @author txy
     * @description
     * @date 2021/11/24 10:04
     */
    @GetMapping("/single.html")
    public String single(@RequestParam Long id, Model model, HttpServletRequest request){
        UserEntity userEntityVO = (UserEntity) request.getSession().getAttribute(Constant.LOGIN_USER);
        Long currentUser = -1L;
        if(userEntityVO!=null){
            currentUser = userEntityVO.getId();
        }
        WaiGuaInfoVO infoVO = infoService.getByIdAndUnmber(id,currentUser);
        List<CommentEntity>  list= commentService.commentsList(id,2,currentUser);
        model.addAttribute("infoVO",infoVO);
        model.addAttribute("list",list);
        log.info("单条详情id:{}",infoVO.getThumbUpNumber());
        return "single";
    }

    /**
     *  描述: 局部刷新，注意返回值
     * @param model:
     * @return String
     * @author txy
     * @description
     * @date 2021/11/25 17:57
     */
    @RequestMapping("/local")
    public String localRefresh(@RequestParam Long id,Model model, HttpServletRequest request) {
        UserEntity userEntityVO = (UserEntity) request.getSession().getAttribute(Constant.LOGIN_USER);
        Long currentUser = -1L;
        if(userEntityVO!=null){
            currentUser = userEntityVO.getId();
        }
        WaiGuaInfoVO infoVO = infoService.getByIdAndUnmber(id,currentUser);
        List<CommentEntity>  list= commentService.commentsList(id,2,currentUser);
        model.addAttribute("infoVO",infoVO);
        model.addAttribute("list",list);
        // "single"single.html的名，
        // "table_refresh"single.html中需要刷新的部分标志,
        // 在标签里加入：th:fragment="table_refresh"
        return "single::table_refresh";
    }

}
