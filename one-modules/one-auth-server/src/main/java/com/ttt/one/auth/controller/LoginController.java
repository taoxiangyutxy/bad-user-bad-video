package com.ttt.one.auth.controller;
import com.alibaba.fastjson.TypeReference;
import com.ttt.one.oplog.annotation.OperationLog;
import com.ttt.one.oplog.annotation.OperationLogType;
import com.ttt.one.auth.fegin.UserFeginServer;
import com.ttt.one.auth.service.AuthService;
import com.ttt.one.auth.utils.TokenUtil;
import com.ttt.one.auth.vo.OperationLogInfo;
import com.ttt.one.auth.vo.UserLoginVo;
import com.ttt.one.auth.vo.UserRegistVo;
import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.utils.R;
import com.ttt.one.common.vo.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
@Tag(name = "登录接口")
@Controller
@RequestMapping("/login")
@RefreshScope
@Slf4j
public class LoginController {
    @Autowired
    private AuthService authService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserFeginServer userFeginServer;

    @Autowired
    private TokenUtil tokenUtil;

    @Value("${spring.ttt.theHost}")
    private String theHost;

    @Value("${spring.ttt.log.url}")
    private String url;
    @Operation(summary = "发送验证码")
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(String phone){
        authService.sendCode(phone);
        return R.ok();
    }
    @Operation(summary = "测试接口")
    @Parameter(name = "info",description ="日志信息类")
    @ResponseBody
    @RequestMapping("/test")
    public String createUserTest(@RequestBody OperationLogInfo info){
        log.info(info.toString());
        return "ok:"+new Date()+ " ---"+url;
    }

    /**
     * RedirectAttributes redirectAttributes 模拟重定向携带数据
     * @param vo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @Operation(summary = "用户注册")
    @Parameter(name = "vo",description ="用户注册实体")
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            /**
             * .map(fieldError -> {
             *                 String field = fieldError.getField();
             *                 String defaultMessage = fieldError.getDefaultMessage();
             *                 errors.put(field,defaultMessage);
             *                 return null;
             *             });
             */
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",errors);
            //Request method 'POST' not supported]  post不支持错误解析
            // 首先发请求用户注册->/regist[post请求]-->转发到reg.html页面(路径映射默认都是get方式才能访问的。) 错误：return "forward:/reg.html"; 解决:  return "reg";

            //校验出错，转发到出错页
            //  return "forward:/reg.html";
           // return "reg";
            return "redirect:http://"+theHost+":88/one-auth-server/login/reg.html";
        }
        //注册
          //1.校验验证码
        String code = vo.getCode();
        String phone = vo.getPhone();
        String s = redisTemplate.opsForValue().get(Constant.SMS_CODE_CACHE_PREFIX + phone);
        if(!StringUtils.isEmpty(s)){
            if(code.equals(s.split("_")[0])){
                //验证码对比成功
                //删除验证码,令牌机制
                redisTemplate.delete(Constant.SMS_CODE_CACHE_PREFIX + phone);
                //调用远程服务注册
                R r = userFeginServer.regist(vo);
                if(r.getCode()==0){
                    //成功
                    return "redirect:http://"+theHost+":88/one-auth-server/login/login.html";
                }else{
                    // TODO 待解决： 手机号重复 这里报错了  msg信息没有展示到前台
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return "redirect:http://"+theHost+":88/one-auth-server/login/reg.html";
                }
            }else{
                Map<String, String> errors = new HashMap<>();
                errors.put("code","验证码错误!");
                redirectAttributes.addFlashAttribute("errors",errors);
                return "redirect:http://"+theHost+":88/one-auth-server/login/reg.html";
            }
        }else{
            Map<String, String> errors = new HashMap<>();
            errors.put("code","验证码错误!");
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://"+theHost+":88/one-auth-server/login/reg.html";
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    @Parameter(name = "vo",description ="用户登录实体")
    @OperationLog(type =OperationLogType.QUERY ,desc = "登录接口")
    public  String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session, HttpServletResponse response){
        log.info("tttUrl:{}",url);
        R r = userFeginServer.login(vo);
        if(r.getCode()==0){
            UserEntity data = r.getData("data", new TypeReference<UserEntity>() {
            });
            session.setAttribute(Constant.LOGIN_USER,data);
            Map<String, String> map = tokenUtil.getToken("123456", "1");
            session.setAttribute("token",map);
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.addHeader("Access-Control-Expose-Headers","token");
            //token放至请求头给前端
            response.addHeader("token",map.get("token"));
            System.out.println("---------------"+map.get("token"));
            log.info("测试info");
            //登录成功
            return "redirect:http://"+theHost+":20000/";
        }else{
            Map<String,String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>() {
            }));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://"+theHost+":88/one-auth-server/login/login.html";
        }

    }

    @GetMapping(value = "/login.html")
    public String loginPage(HttpSession session) {

        //从session先取出来用户的信息，判断用户是否已经登录过了
        Object attribute = session.getAttribute(Constant.LOGIN_USER);
        //如果用户没登录那就跳转到登录页面
        if (attribute == null) {
            return "login";
        } else {
            return "redirect:http://127.0.0.1:20000";
        }
    }

    @GetMapping(value = "/logout.html")
    @Operation(summary = "退出登录")
    @OperationLog(type =OperationLogType.QUERY ,desc = "退出登录接口")
    public String logout(HttpServletRequest request) {
         request.getSession().removeAttribute(Constant.LOGIN_USER);
         request.getSession().invalidate();
        return "redirect:http://127.0.0.1:20000";
     }
}
