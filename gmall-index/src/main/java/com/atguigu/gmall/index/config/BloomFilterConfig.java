package com.atguigu.gmall.index.config;

import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author zqq
 * @create 2021-02-21 15:00
 */
@Configuration
public class BloomFilterConfig {
    public static final  String KEY_PREFIX="index:categories:";

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Bean
    public RBloomFilter bloomFilter(){
        //初始化Bloomfilter
        RBloomFilter<Object> bloomFilter = this.redissonClient.getBloomFilter("index:bloom");
        bloomFilter.tryInit(4000,0.01);


        // 给布隆过滤器添加初始化数据
        List<CategoryEntity> categoryEntities = this.gmallPmsClient.getCategorysByParentId(0l).getData();
        if (!CollectionUtils.isEmpty(categoryEntities)){
            categoryEntities.forEach(categoryEntity -> {
                bloomFilter.add(KEY_PREFIX+"["+categoryEntity.getId()+"]");
            });
        }
        return bloomFilter;
    }
}
