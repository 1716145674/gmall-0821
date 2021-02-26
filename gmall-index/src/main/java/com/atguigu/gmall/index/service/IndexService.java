package com.atguigu.gmall.index.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author zqq
 * @create 2021-02-02 20:48
 */
@Service
public class IndexService {

    public static final  String KEY_PREFIX="index:categories:";
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private GmallPmsClient pmsClient;


    public List<CategoryEntity> queryLv1Categories() {

        List<CategoryEntity> categoryEntities = pmsClient.getCategorysByParentId(0l).getData();
        return categoryEntities;
    }

    @GmallCache(prefix = KEY_PREFIX,timeout = 5*24*60,lock ="index:categories:lock")
    public List<CategoryEntity> queryLv2CategoriesByParentId(Long pid) {
        List<CategoryEntity> categoryEntities = pmsClient.queryCategoriesWithSubsByPid(pid).getData();
        return  categoryEntities;
    }
    public List<CategoryEntity> queryLv2CategoriesByParentId2(Long pid) {
        // 先查询缓存,缓存中有,直接返回

        String json=this.redisTemplate.opsForValue().get(KEY_PREFIX+pid);
        if (StringUtils.isNotBlank(json)){
            return JSON.parseArray(json,CategoryEntity.class);
        }
        // 为了防止缓存击穿,添加分布式锁
        RLock lock = redissonClient.getLock("index:categories:lock" + pid);
        lock.lock();
        // 双重检查再次查询缓存,因为可能上一个以及把数据放到缓存中了,没必要再查询数据库了
        String json2=this.redisTemplate.opsForValue().get(KEY_PREFIX+pid);
        if (StringUtils.isNotBlank(json2)){
            return JSON.parseArray(json2,CategoryEntity.class);
        }

        // 查询数据库拿到数据
        List<CategoryEntity> categoryEntities = pmsClient.queryCategoriesWithSubsByPid(pid).getData();

        //防止缓存穿透 为空的数据也要缓存到缓存中
        if (CollectionUtils.isEmpty(categoryEntities)){
            redisTemplate.opsForValue().set(KEY_PREFIX+pid,"Null",3, TimeUnit.MINUTES);
        }else {
            // 为了防止缓存雪崩 放入缓存时为每个缓存添加随机的过期时间
            redisTemplate.opsForValue().set(KEY_PREFIX+pid,JSON.toJSONString(categoryEntities),5*24+new Random().nextInt(10), TimeUnit.HOURS);
        }
        return  categoryEntities;

    }
}
