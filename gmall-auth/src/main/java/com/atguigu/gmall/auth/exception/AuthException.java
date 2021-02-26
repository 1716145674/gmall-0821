package com.atguigu.gmall.auth.exception;

/**
 * @author zqq
 * @create 2021-02-23 19:00
 */
public class AuthException extends RuntimeException {
    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }
}
