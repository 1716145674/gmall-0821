package com.atguigu.gmall.cart.exception;

/**
 * @author zqq
 * @create 2021-02-24 17:54
 */
public class CartException extends RuntimeException {
    public CartException() {
        super();
    }

    public CartException(String message) {
        super(message);
    }
}
