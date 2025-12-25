package com.ttt.one.admin.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.ttt.one.common.exception.BizException;
import com.ttt.one.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器  每个微服务一个最好
 * 统一处理各类异常并返回标准格式的响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 专门处理认证异常（401）
    @ExceptionHandler(AuthenticationException.class)
    public R handleAuthenticationException(AuthenticationException e) {
        log.error("认证异常：{}", e.getMessage(), e);
        return R.error(401, "未认证，请先登录");
    }

    // 2. 专门处理权限异常（403）
    @ExceptionHandler(AccessDeniedException.class)
    public R handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限异常：{}", e.getMessage(), e);
        return R.error(403, "权限不足，无法访问此资源");
    }

    /**
     * 处理算术异常（如除零错误）
     * @param e 算术异常
     * @return 错误响应
     */
    @ExceptionHandler(ArithmeticException.class)
    public R handleArithmeticException(ArithmeticException e) {
        log.error("算术异常：{}", e.getMessage(), e);
        return R.error(500, "计算错误：" + e.getMessage());
    }


    /**
     * 处理业务异常
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BizException.class)
    public R handleBizException(BizException e) {
        log.error("业务异常：code={}, msg={}", e.getCode(), e.getMsg(), e);
        return R.error(e.getCode(), e.getMsg());
    }

    /**
     * 处理参数校验异常
     * @param ex 参数校验异常
     * @return 错误响应，包含详细的字段错误信息
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();

        // 使用Stream简化错误信息收集
        Map<String, String> errorMap = result.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing // 如果有重复字段，保留第一个错误
                ));

        log.warn("参数校验失败：{}", errorMap);
        return R.error(400, "参数校验失败", errorMap);
    }

    /**
     * 处理数据绑定失败
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public R handleBindException(BindException e) {
        BindingResult result = e.getBindingResult();

        Map<String, String> errorMap = result.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));

        log.warn("数据绑定失败：{}", errorMap);
        return R.error(400, "数据绑定失败", errorMap);
    }

    /**
     * 处理所有未被捕获的异常（兜底处理）
     * @param e 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(Throwable.class)
    public R handleException(Throwable e) {
        log.error("未知异常：{}", e.getClass().getName(), e);
        // 避免将系统内部错误信息暴露给用户
        String message = e.getMessage();
        if(message == null || message.isEmpty()){
            message = "系统内部错误，请联系管理员";
        }
        return R.error(500, message);
    }


    /**
     * 处理 JSON 反序列化异常（类型不匹配）
     * 例如：将字符串 "asd" 转换为 Integer
     * 这种情况发生在参数绑定之前，@Valid 注解不会生效
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("JSON反序列化异常: {}", ex.getMessage(), ex);

        // 检查是否是 InvalidFormatException（类型转换错误）
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;

            // 获取出错的字段名
            String fieldName = "未知字段";
            if (ife.getPath() != null && !ife.getPath().isEmpty()) {
                fieldName = ife.getPath().stream()
                        .map(ref -> ref.getFieldName())
                        .reduce((first, second) -> second)  // 取最后一个
                        .orElse("未知字段");
            }

            // 获取期望的类型
            String expectedType = ife.getTargetType() != null ?
                    ife.getTargetType().getSimpleName() : "未知类型";

            // 获取实际传入的值
            Object value = ife.getValue();
            String valueStr = value != null ? value.toString() : "null";

            String errorMsg = String.format("字段 '%s' 类型错误，期望 %s 类型，但收到 '%s'",
                    fieldName, expectedType, valueStr);

            return R.error(400, errorMsg);
        } else {
            // 其他类型的 JSON 解析错误
            return R.error(400, "请求参数格式错误，请检查JSON格式");
        }
    }
}
