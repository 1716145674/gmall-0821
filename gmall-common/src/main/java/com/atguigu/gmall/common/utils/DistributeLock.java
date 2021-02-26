package com.atguigu.gmall.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author zqq
 * @create 2021-02-04 19:25
 */
@Component
@Slf4j
public class DistributeLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean tryLock(String lockName, String uuid, Integer expire) {

        // 加锁的lua脚本
        String script = "if(redis.call('exists',KEYS[1])== 0 or redis.call('hexists',KEYS[1],ARGV[1])==1) then " +
                "redis.call('hincrby',KEYS[1],ARGV[1],1) " +
                "redis.call('expire',KEYS[1],ARGV[2])" +
                "return 1 " +
                "else return 0 " +
                "end";
        Boolean flag = redisTemplate.execute(new DefaultRedisScript<Boolean>(script, Boolean.class), Arrays.asList(lockName), uuid, expire);
        if (flag) {
            reExpire(lockName, uuid, expire);
            return true;
        } else {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tryLock(lockName, uuid, expire);
        }

        return true;
    }

    // 添加自动续时功能
    Timer timer = new Timer();
    private void reExpire(String lockName, String uuid, Integer expire) {


        String script = "if(redis.call('hexists',KEYS[1],ARGV[1])==1) then " +
                "redis.call('expire',KEYS[1],ARGV[2]) " +
                "return 1 " +
                "else return 0 " +
                "end";
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Boolean flag = redisTemplate.execute(new DefaultRedisScript<Boolean>(script, Boolean.class), Arrays.asList(lockName), uuid, expire);
                if (!flag){
                    timer.cancel();
                }
            }
        }, expire * 1000 / 3, expire * 1000 / 3);
    }
    public void unlock(String lockName, String uuid){
        String script = "if(redis.call('hexists',KEYS[1],ARGV[1])==1) then " +
                            "if( redis.call('hincrby',KEYS[1],ARGV[1],-1)==0 ) then " +
                                " redis.call('del',KEYS[1]) " +
                                " return 0 " +
                            " else return redis.call('hincrby',KEYS[1],ARGV[1],-1) " +
                        "else return '-1' " +
                        "end";
        Integer flag = redisTemplate.execute(new DefaultRedisScript<Integer>(script, Integer.class), Arrays.asList(lockName), uuid);
        if (flag==-1){
            log.error("分布式事务解锁失败了 锁名是: {},uuid是: {}",lockName,uuid);
        }else  if (flag==0){
            timer.cancel();
        }
    }
}

