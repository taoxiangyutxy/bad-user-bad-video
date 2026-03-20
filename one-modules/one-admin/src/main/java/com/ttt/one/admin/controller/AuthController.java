package com.ttt.one.admin.controller;

import com.ttt.one.admin.entity.SysRole;
import com.ttt.one.admin.entity.SysUser;
import com.ttt.one.admin.service.SysRoleService;
import com.ttt.one.admin.service.SysUserService;
import com.ttt.one.admin.utils.JwtTokenProvider;
import com.ttt.one.admin.utils.JwtUtil;
import com.ttt.one.admin.vo.SysUserVO;
import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.utils.R;
import com.ttt.one.thirdparty.component.EmailComponent;
import com.ttt.one.thirdparty.service.VerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@Tag(name = "登录")
@RequestMapping("/api/admin")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final SysUserService sysUserService;
    private final UserDetailsService userDetailsService;
    private final SysRoleService sysRoleService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private final EmailComponent emailComponent;
    
    private final VerificationCodeService verificationCodeService;

    @PostMapping("/register")
    @Operation(summary = "注册")
    public R register(@RequestBody SysUserVO userVO, BindingResult result){
        SysUser user = sysUserService.createUser(userVO);
        if(user.getUserId() == null){
            R.error(400, "注册失败", "");
        }
        return R.ok().put("data",user);
    }
    @PostMapping("/login")
    public R login(@RequestBody SysUserVO userVO,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return R.error(400, "登录失败", errors);
        }

        try {
            // 1. 认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userVO.getUsername(),
                            userVO.getPassword()
                    )
            );

            // 2. 设置认证
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. 生成Token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(userDetails);

            // 4. 获取用户信息
            SysUser user = sysUserService.login(userVO);

            // 6. 返回结果

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("tokenType", "Bearer");
            data.put("expiresIn", 120000); // 24小时
            data.put("user", user);

            log.info("用户登录成功: {}", user.getUsername());
            return R.ok().put("data",data);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("登录失败: {}", e.getMessage());
            return R.error(401, "登录失败", e.getMessage());
        }
    }
    @PostMapping("/login1")
    @Operation(summary = "登录")
    public String login1(SysUserVO userVO, RedirectAttributes redirectAttributes,
                       HttpSession session, HttpServletResponse response){
        Map<String, String> errors = new HashMap<>();

        if(ObjectUtils.isEmpty(userVO)||(ObjectUtils.isEmpty(userVO.getUsername()) || ObjectUtils.isEmpty(userVO.getPassword()))){
            errors.put("msg", "用户名或密码不能为空");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "false";
        }
        SysUser user = sysUserService.login(userVO);
        //登录成功
        if(user != null){
            //获取角色及权限
            List<SysRole> sysRoles = sysRoleService.findByUserId(user.getUserId());
            List<String> roles = sysRoles.stream().map(SysRole::getRoleName).collect(Collectors.toList());
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            List<String> perms = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            session.setAttribute(Constant.LOGIN_SYS_USER, user);

            // 生成token并设置到响应头
            String token = jwtUtil.generateToken(user.getUserId(),user.getUsername(),roles);
            session.setAttribute("token", token);
            session.setAttribute("roles", roles);
            session.setAttribute("perms", perms);

            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.addHeader("Access-Control-Expose-Headers", "token");
            response.addHeader("token", token);
            return "true";
        }
        errors.put("msg", "账号出错！！！");
        redirectAttributes.addFlashAttribute("errors", errors);
        return "false";
    }


    /**
     * 退出登录
     *
     * @param request 请求
     * @return 重定向路径
     */
    @GetMapping(value = "/logout.html")
    @Operation(summary = "退出登录")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute(Constant.LOGIN_SYS_USER);
        session.invalidate();
        return "redirect:/api/admin/login.html?logout=true";
    }


    @PostMapping("/load-user")
    public R manuallyLoadUser(@RequestParam String username) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            List<String> perms = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            return  R.ok().put("data",authentication);

        } catch (Exception e) {
            e.printStackTrace();
         return    R.error(500, e.getMessage());
        }
    }
    @Operation(summary = "获取当前用户")
    @GetMapping("/get-current-user")
    public R getCurrentUser() {
        try {
            SysUser user = sysUserService.getCurrentUser();
            return R.ok().put("data", user);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(500, e.getMessage());
        }
    }

    /**
     * 请求密码重置验证码
     *
     * @param username 用户名
     * @return 操作结果
     */
    @Operation(summary = "请求密码重置验证码")
    @PostMapping("/forgot-password")
    @ResponseBody
    public R forgotPassword(@RequestParam String username) {
        try {
            // 1. 验证用户名是否存在
            SysUser user = sysUserService.getUserByUsername(username);
            if (user == null) {
                return R.error("用户不存在");
            }

            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                return R.error("用户未绑定邮箱");
            }

            // 2. 检查是否可以发送验证码
            String emailKey = "admin:email:" + user.getEmail();
            if (!verificationCodeService.canSendCode(emailKey)) {
                return R.error("请求过于频繁，请稍后再试");
            }

            // 3. 生成验证码
            String verificationCode = verificationCodeService.generateVerificationCode();
            
            // 4. 存储验证码
            verificationCodeService.storeVerificationCode(username, verificationCode);
            
            // 5. 发送邮件
            emailComponent.sendPasswordResetEmail(user.getEmail(), username, verificationCode);
            
            return R.ok("验证码已发送到您的邮箱");
        } catch (Exception e) {
            log.error("Forgot password error", e);
            return R.error("发送验证码失败：" + e.getMessage());
        }
    }

    /**
     * 重置密码
     *
     * @param username 用户名
     * @param verificationCode 验证码
     * @param newPassword 新密码
     * @return 操作结果
     */
    @Operation(summary = "重置密码")
    @PostMapping("/reset-password")
    @ResponseBody
    public R resetPassword(@RequestParam String username, 
                          @RequestParam String verificationCode,
                          @RequestParam String newPassword) {
        try {
            // 1. 验证密码强度
            if (newPassword == null || newPassword.length() < 8) {
                return R.error("密码长度至少8位");
            }

            // 2. 验证验证码
            if (!verificationCodeService.verifyCode(username, verificationCode)) {
                return R.error("验证码错误或已过期");
            }

            // 3. 更新密码
            sysUserService.resetPassword(username, newPassword);
            return R.ok("密码重置成功");
        } catch (Exception e) {
            log.error("Reset password error", e);
            return R.error("密码重置失败：" + e.getMessage());
        }
    }
}
