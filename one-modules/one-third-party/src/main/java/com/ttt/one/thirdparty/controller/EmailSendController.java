package com.ttt.one.thirdparty.controller;

import com.ttt.one.thirdparty.component.EmailComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 邮件发送控制器
 */
@RestController
@RequestMapping("/api/email")
public class EmailSendController {

    @Autowired
    private EmailComponent emailComponent;

    /**
     * 发送密码重置验证码邮件
     *
     * @param to              收件人邮箱
     * @param username        用户名
     * @param verificationCode 验证码
     * @return 发送结果
     */
    @PostMapping("/send-password-reset")
    public String sendPasswordResetEmail(
            @RequestParam String to,
            @RequestParam String username,
            @RequestParam String verificationCode) {
        
        try {
            emailComponent.sendPasswordResetEmail(to, username, verificationCode);
            return "验证码已发送到邮箱：" + to;
        } catch (Exception e) {
            return "发送失败：" + e.getMessage();
        }
    }

    /**
     * 发送简单文本邮件（测试用）
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 发送结果
     */
    @PostMapping("/send-simple")
    public String sendSimpleEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String content) {
        
        try {
            emailComponent.sendSimpleEmail(to, subject, content);
            return "邮件已发送到：" + to;
        } catch (Exception e) {
            return "发送失败：" + e.getMessage();
        }
    }
}