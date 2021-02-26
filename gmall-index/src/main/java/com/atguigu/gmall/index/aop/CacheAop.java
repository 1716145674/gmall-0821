package com.atguigu.gmall.index.aop;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.google.common.hash.BloomFilter;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * @author zqq
 * @create 2021-02-18 15:53
 */
@Aspect
@Component
public class CacheAop {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RBloomFilter bloomFilter;

    @Around("@annotation(com.atguigu.gmall.index.annotation.GmallCache)")
    public Object  cacheAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 拿到注解的属性信息
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        GmallCache gmallCache = (GmallCache)signature.getMethod().getAnnotations()[0];
        //获得前缀
        String prefix = gmallCache.prefix();
        //拿到所有的参数封装成一个str
        String joinStr = Arrays.asList(proceedingJoinPoint.getArgs()).toString();
        // 拼装为key
        String key=prefix+joinStr;
        // 拿到锁的前缀
        String lock = gmallCache.lock();
        //拿到过期时间
        int timeout = gmallCache.timeout();
        //拿到过期时间的随机值
        int random=gmallCache.random();
        // 拿到方法的返回值类型
        Class returnType = signature.getReturnType();

        if (!this.bloomFilter.contains(key)) {
            return null;
        }
        // 先查询缓存,缓存中有,直接返回
        String json=this.redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(json)){
            return JSON.parseObject(json, returnType);
        }

        // 为了防止缓存击穿,添加分布式锁
        RLock rLock = redissonClient.getFairLock(lock + joinStr);
        rLock.lock();

        try {
            // 双重检查再次查询缓存,因为可能上一个以及把数据放到缓存中了,没必要再查询数据库了
            String json2=this.redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotBlank(json2)){
                return JSON.parseObject(json2, returnType);
            }

            // 执行目标方法
            Object result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());



            //防止缓存穿透 为空的数据也要缓存到缓存中
            if (result==null){
                redisTemplate.opsForValue().set(key,"Null",3, TimeUnit.MINUTES);
            }else {
                // 为了防止缓存雪崩 放入缓存时为每个缓存添加随机的过期时间
                redisTemplate.opsForValue().set(key,JSON.toJSONString(result),timeout+new Random().nextInt(random), TimeUnit.MINUTES);
            }
            return  result;
        } finally {
            //解锁
            rLock.unlock();
        }
    }
}
