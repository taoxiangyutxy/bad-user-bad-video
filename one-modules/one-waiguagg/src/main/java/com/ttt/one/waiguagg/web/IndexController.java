package com.ttt.one.waiguagg.web;

import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.vo.UserEntity;
import com.ttt.one.waiguagg.entity.CommentEntity;
import com.ttt.one.waiguagg.entity.InfoEntity;
import com.ttt.one.waiguagg.service.CommentService;
import com.ttt.one.waiguagg.service.InfoService;
import com.ttt.one.waiguagg.vo.WaiGuaInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
/**
 * 首页控制器
 *
 * 提供网站首页、外挂信息详情页等前端页面的路由
 */
@Tag(name = "首页", description = "首页相关接口")
@Slf4j
@Controller
@RequiredArgsConstructor
public class IndexController {

    private final InfoService infoService;
    private final CommentService commentService;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 测试MQ消息发送功能
     *
     * @return 测试结果
     */
    @Operation(summary = "测试MQ", description = "测试RabbitMQ消息发送功能")
    @ResponseBody
    @RequestMapping("/test")
    public String createUserTest() {
        InfoEntity entity = new InfoEntity();
        entity.setWaiguaUsername("测试测试");
        entity.setWaiguaDescribe("MQMQMQMQMQ");
        entity.setCreateTime(new java.util.Date());
        // 消息发送给 create.ttt
        rabbitTemplate.convertAndSend("topic-exchange", "create.ttt", entity);
        return "ok:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    /**
     * 首页展示
     *
     * @param params 查询参数
     * @param model  模型对象
     * @return 首页视图
     */
    @Operation(summary = "首页", description = "展示外挂信息列表首页")
    @GetMapping({"/", "/index.html"})
    public String indexPage(@RequestParam Map<String, Object> params, Model model) {
        List<WaiGuaInfoVO> lists = infoService.pageAllWaiGua(params);
        model.addAttribute("lists", lists);
        // 有默认前缀和默认后缀，所以直接返回index即可，视图解析器会自动拼接
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
    @Operation(summary = "单条外挂信息", description = "展示单条外挂信息详情页")
    @Parameter(name = "id", description = "外挂信息ID")
    @GetMapping("/single.html")
    public String single(@RequestParam Long id, Model model, HttpServletRequest request){
        UserEntity userEntityVO = (UserEntity) request.getSession().getAttribute(Constant.LOGIN_USER);
        Long currentUser = -1L;
        if (userEntityVO != null) {
            currentUser = userEntityVO.getId();
        }
        WaiGuaInfoVO infoVO = infoService.getByIdAndUnmber(id, currentUser);
        List<CommentEntity> list = commentService.commentsList(id, 2, currentUser);
        model.addAttribute("infoVO", infoVO);
        model.addAttribute("list", list);
        log.info("单条详情id:{}", infoVO.getThumbUpNumber());
        return "single";
    }

    /**
     * 局部刷新接口
     *
     * @param id      外挂信息ID
     * @param model   模型对象
     * @param request HTTP请求对象
     * @return 局部刷新片段
     */
    @Operation(summary = "局部刷新", description = "用于页面局部刷新")
    @Parameter(name = "id", description = "外挂信息ID")
    @RequestMapping("/local")
    public String localRefresh(@RequestParam Long id, Model model, HttpServletRequest request) {
        UserEntity userEntityVO = (UserEntity) request.getSession().getAttribute(Constant.LOGIN_USER);
        Long currentUser = -1L;
        if (userEntityVO != null) {
            currentUser = userEntityVO.getId();
        }
        WaiGuaInfoVO infoVO = infoService.getByIdAndUnmber(id, currentUser);
        List<CommentEntity> list = commentService.commentsList(id, 2, currentUser);
        model.addAttribute("infoVO", infoVO);
        model.addAttribute("list", list);
        // "single" 是 single.html 的名称
        // "table_refresh" 是 single.html 中需要刷新的部分标识
        // 在标签中使用：th:fragment="table_refresh"
        return "single::table_refresh";
    }

}
