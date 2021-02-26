package com.atguigu.gmall.item.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zqq
 * @create 2021-02-21 16:48
 */
@Configuration
@Slf4j
public class ThreadPoolConfig {


    @Bean
    public ThreadPoolExecutor threadPoolExecutor(
            @Value("${pool.coreSize}") Integer coreSize,
            @Value("${pool.maxSize}") Integer maxSize,
            @Value("${pool.timeOut}") Integer timeOut,
            @Value("${pool.blockQueueSize}") Integer blockQueueSize
    ){
        log.info(coreSize.getClass().getName()+coreSize);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(coreSize,maxSize,timeOut, TimeUnit.SECONDS,new ArrayBlockingQueue<>(blockQueueSize));
        return threadPoolExecutor;
    }
}
