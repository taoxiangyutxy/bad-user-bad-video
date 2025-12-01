package com.ttt.one.common.exception;

import com.ttt.one.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各类异常并返回标准格式的响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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
}