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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}