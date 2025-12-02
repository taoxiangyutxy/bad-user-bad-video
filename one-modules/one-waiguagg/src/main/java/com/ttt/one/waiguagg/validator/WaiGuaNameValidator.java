package com.ttt.one.waiguagg.validator;

import com.ttt.one.waiguagg.annotation.WaiGuaName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WaiGuaNameValidator implements ConstraintValidator<WaiGuaName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 判断字符串是否只包含英文字母（不区分大小写）和数字
        return value.matches("^[a-zA-Z0-9]+$");
    }
}
