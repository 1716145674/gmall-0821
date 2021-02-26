package com.atguigu.gmall.cart.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author zqq
 * @create 2021-02-26 16:35
 */
@Slf4j
@Component
public class CartAsyncException implements AsyncUncaughtExceptionHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final  String CART_ASYNC_EXCEPTION="cart:asyncException";

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
      log.error("购物车异步操作出错了,异常是{} ,出错的方法是{},参数是{}",throwable.getMessage(),method.getName(), Arrays.asList(objects));
      // 将出错的信息 放到redis中 供以后定时任务同步
        SetOperations<String, String> set = this.redisTemplate.opsForSet();
        String userId = objects[0].toString();
        set.add(CART_ASYNC_EXCEPTION,userId);
    }
}
