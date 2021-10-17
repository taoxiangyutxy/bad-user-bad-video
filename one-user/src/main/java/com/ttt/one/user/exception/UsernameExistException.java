package com.ttt.one.user.exception;

/**
 * 用户名已存在异常
 */
public class UsernameExistException extends RuntimeException{
    public UsernameExistException() {
        super("用户名已存在");
    }
}
