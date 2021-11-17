package com.ttt.one.auth.controller;
import com.alibaba.fastjson.TypeReference;
import com.ttt.one.auth.fegin.UserFeginServer;
import com.ttt.one.auth.service.AuthService;
import com.ttt.one.auth.vo.UserLoginVo;
import com.ttt.one.auth.vo.UserRegistVo;
import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.utils.R;
import com.ttt.one.common.vo.UserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LoginController {
    @Autowired
    private AuthService authService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserFeginServer userFeginServer;

    /*@GetMapping("/login.html")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/reg.html")
    public String regPage(){
        return "reg";
    }*/
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(String phone){
        authService.sendCode(phone);
        return R.ok();
    }

    /**
     * RedirectAttributes redirectAttributes 模拟重定向携带数据
     * @param vo
     * @param result
     * @param redirectAttributes
     * @return
     */
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
            return "redirect:http://auth.waiguattt.com/reg.html";
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
                    return "redirect:http://auth.waiguattt.com/login.html";
                }else{
                    // TODO 待解决： 手机号重复 这里报错了  msg信息没有展示到前台
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return "redirect:http://auth.waiguattt.com/reg.html";
                }
            }else{
                Map<String, String> errors = new HashMap<>();
                errors.put("code","验证码错误!");
                redirectAttributes.addFlashAttribute("errors",errors);
                return "redirect:http://auth.waiguattt.com/reg.html";
            }
        }else{
            Map<String, String> errors = new HashMap<>();
            errors.put("code","验证码错误!");
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.waiguattt.com/reg.html";
        }

        //注册成功回到登录页
      //  return "redirect:/login.html";
    }

    @PostMapping("/login")
    public  String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session){
        R r = userFeginServer.login(vo);
        if(r.getCode()==0){
            UserEntity data = r.getData("data", new TypeReference<UserEntity>() {
            });
            session.setAttribute(Constant.LOGIN_USER,data);
            //登录成功
            return "redirect:http://waiguattt.com";
        }else{
            Map<String,String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>() {
            }));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.waiguattt.com/login.html";
        }

    }
}
