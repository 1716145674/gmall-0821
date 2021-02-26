package com.atguigu.gmall.index.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author zqq
 * @create 2021-02-18 15:40
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {
    /**
     * 缓存的前缀
     * 将来的缓存key为前缀 +方法参数
     * @return
     */
    String prefix() default "prefix";

    /**
     * 缓存的过期时间  默认值为5分钟
     * @return
     */
    int timeout() default 5;

    /**
     * 为了防止缓存雪崩
     * 给缓存指定随机值范围
     * @return
     */
    int random() default 5;

    /**
     * 为了防止缓存击穿 添加分布式锁
     * 此处指定锁的lock前缀
     * @return
     */
    String lock() default "lock";


}
