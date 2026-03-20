package com.ttt.one.thirdparty.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 邮件发送组件
 */
@Slf4j
@Component
public class EmailComponent {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendSimpleEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        
        mailSender.send(message);
    }

    /**
     * 发送密码重置验证码邮件
     *
     * @param to              收件人邮箱
     * @param username        用户名
     * @param verificationCode 验证码
     */
    @Async
    public void sendPasswordResetEmail(String to, String username, String verificationCode) {
        try {
            String subject = "密码重置验证码";
            String content = String.format(
                "尊敬的用户 %s：\n\n" +
                "您请求重置密码，验证码为：%s\n\n" +
                "该验证码15分钟内有效，请尽快完成密码重置。\n\n" +
                "如果您没有请求重置密码，请忽略此邮件。\n\n" +
                "此邮件为系统自动发送，请勿回复。",
                username, verificationCode
            );
            
            sendSimpleEmail(to, subject, content);
            log.info("密码重置邮件发送成功，收件人：{}", to);
        } catch (Exception e) {
            log.error("发送密码重置邮件失败，收件人：{}", to, e);
        }
    }
}