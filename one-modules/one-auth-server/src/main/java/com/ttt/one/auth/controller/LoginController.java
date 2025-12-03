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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;
    private final StringRedisTemplate redisTemplate;
    private final UserFeginServer userFeginServer;
    private final TokenUtil tokenUtil;

    @Value("${spring.ttt.theHost}")
    private String theHost;

    @Value("${spring.ttt.log.url}")
    private String url;

    private static final String REDIRECT_REG_URL = "redirect:http://%s:88/one-auth-server/reg.html";
    private static final String REDIRECT_LOGIN_URL = "redirect:http://%s:88/one-auth-server/login/login.html";
    private static final String REDIRECT_HOME_URL = "redirect:http://%s:20000/";
    private static final String VERIFICATION_CODE_ERROR = "验证码错误!";
    private static final String ERROR_KEY_MSG = "msg";
    private static final String ERROR_KEY_CODE = "code";
    @Operation(summary = "发送验证码")
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam String phone) {
        authService.sendCode(phone);
        return R.ok();
    }
    @Operation(summary = "测试接口")
    @Parameter(name = "info", description = "日志信息类")
    @ResponseBody
    @PostMapping("/test")
    public String createUserTest(@RequestBody OperationLogInfo info) {
        log.info("Operation log info: {}", info);
        return "ok:" + new Date() + " ---" + url;
    }

    /**
     * 用户注册
     *
     * @param vo                 用户注册实体
     * @param result             验证结果
     * @param redirectAttributes 重定向属性
     * @return 重定向路径
     */
    @Operation(summary = "用户注册")
    @Parameter(name = "vo", description = "用户注册实体")
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        // 1. 校验参数
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            return String.format(REDIRECT_REG_URL, theHost);
        }

        // 2. 校验验证码
        String code = vo.getCode();
        String phone = vo.getPhone();
        String redisCode = redisTemplate.opsForValue().get(Constant.SMS_CODE_CACHE_PREFIX + phone);
        
        /*if (StringUtils.isEmpty(redisCode)) {
            return redirectWithError(redirectAttributes, ERROR_KEY_CODE, VERIFICATION_CODE_ERROR, REDIRECT_REG_URL);
        }

        String storedCode = redisCode.split("_")[0];
        if (!code.equals(storedCode)) {
            return redirectWithError(redirectAttributes, ERROR_KEY_CODE, VERIFICATION_CODE_ERROR, REDIRECT_REG_URL);
        }*/

        // 3. 验证码验证成功，删除验证码（令牌机制）
        redisTemplate.delete(Constant.SMS_CODE_CACHE_PREFIX + phone);

        // 4. 调用远程服务注册
        R r = userFeginServer.regist(vo);
        if (r.getCode() == 0) {
            return String.format(REDIRECT_LOGIN_URL, theHost);
        } else {
            String errorMsg = r.getData(ERROR_KEY_MSG, new TypeReference<String>() {});
            return redirectWithError(redirectAttributes, ERROR_KEY_MSG, errorMsg, REDIRECT_REG_URL);
        }
    }

    /**
     * 用户登录
     *
     * @param vo                 用户登录实体
     * @param redirectAttributes 重定向属性
     * @param session            会话
     * @param response           响应
     * @return 重定向路径
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    @Parameter(name = "vo", description = "用户登录实体")
    @OperationLog(type = OperationLogType.QUERY, desc = "登录接口")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, 
                       HttpSession session, HttpServletResponse response) {
        log.info("User login attempt, url config: {}", url);
        
        R r = userFeginServer.login(vo);
        if (r.getCode() == 0) {
            // 登录成功
            UserEntity userData = r.getData("data", new TypeReference<UserEntity>() {});
            session.setAttribute(Constant.LOGIN_USER, userData);
            
            // 生成token并设置到响应头
            Map<String, String> tokenMap = tokenUtil.getToken("123456", "1");
            session.setAttribute("token", tokenMap);
            
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.addHeader("Access-Control-Expose-Headers", "token");
            response.addHeader("token", tokenMap.get("token"));
            
            log.info("User login successful, token: {}", tokenMap.get("token"));
            return String.format(REDIRECT_HOME_URL, theHost);
        } else {
            // 登录失败
            String errorMsg = r.getData(ERROR_KEY_MSG, new TypeReference<String>() {});
            return redirectWithError(redirectAttributes, ERROR_KEY_MSG, errorMsg, REDIRECT_LOGIN_URL);
        }
    }

    /**
     * 登录页面
     *
     * @param session 会话
     * @return 页面路径
     */
    @GetMapping(value = "/login.html")
    public String loginPage(HttpSession session) {
        // 检查用户是否已登录
        Object loginUser = session.getAttribute(Constant.LOGIN_USER);
        if (loginUser == null) {
            return "login";
        } else {
            return "redirect:http://127.0.0.1:20000";
        }
    }

    /**
     * 退出登录
     *
     * @param request 请求
     * @return 重定向路径
     */
    @GetMapping(value = "/logout.html")
    @Operation(summary = "退出登录")
    @OperationLog(type = OperationLogType.QUERY, desc = "退出登录接口")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute(Constant.LOGIN_USER);
        session.invalidate();
        return "redirect:http://127.0.0.1:20000";
    }

    /**
     * 重定向并携带错误信息
     *
     * @param redirectAttributes 重定向属性
     * @param errorKey          错误键
     * @param errorMsg          错误消息
     * @param redirectUrlFormat 重定向URL格式
     * @return 重定向路径
     */
    private String redirectWithError(RedirectAttributes redirectAttributes, String errorKey, 
                                    String errorMsg, String redirectUrlFormat) {
        Map<String, String> errors = new HashMap<>();
        errors.put(errorKey, errorMsg);
        redirectAttributes.addFlashAttribute("errors", errors);
        return String.format(redirectUrlFormat, theHost);
    }
}
