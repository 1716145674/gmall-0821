package com.atguigu.gmall.auth;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author zqq
 * @create 2021-02-23 22:46
 */
@Component
public class ThreadLocalTest {
    public static final ThreadLocal<String> THREAD_LOCAL=new ThreadLocal<>();

    public void test(){

        THREAD_LOCAL.set("zhangsan"+new Random().nextInt(10));
        System.out.println(THREAD_LOCAL.hashCode());
        System.out.println(Thread.currentThread().getName());
        System.out.println("=============");
    }

}
