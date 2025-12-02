package com.ttt.one.waiguagg.annotation;

import com.ttt.one.waiguagg.validator.WaiGuaNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// 校验注解 绑定 校验器
@Documented
@Constraint(validatedBy = {WaiGuaNameValidator.class})  //校验器去真正完成校验功能。
@Target({ FIELD })
@Retention(RUNTIME)
public @interface WaiGuaName {
    String message() default "{jakarta.validation.constraints.NotNull.message}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
