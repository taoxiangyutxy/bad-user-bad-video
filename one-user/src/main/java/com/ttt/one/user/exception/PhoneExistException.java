package com.ttt.one.user.exception;

/**
 * 手机号已存在异常
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("手机号已存在");
    }
}
