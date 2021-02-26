package com.atguigu.gmall.cart.config;

import com.atguigu.gmall.cart.exception.CartAsyncException;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

/**
 * @author zqq
 * @create 2021-02-26 16:36
 */
@Configuration
public class CartAsyncConfig implements AsyncConfigurer {
    @Autowired
    private CartAsyncException cartAsyncException;
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return cartAsyncException;
    }
}
