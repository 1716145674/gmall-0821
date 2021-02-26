package com.atguigu.gmall.scheduling;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.scheduling.feign.GmallPmsClient;
import com.atguigu.gmall.scheduling.mapper.CartMapper;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.List;

@SpringBootTest
class GmallSchedulingApplicationTests {
    public static final String KEY_PREFIX = "index:categories:";
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Test
    void contextLoads() {
        RBloomFilter<Object> bloomFilter = this.redissonClient.getBloomFilter("index:bloom");
        bloomFilter.delete();
        bloomFilter.tryInit(4000, 0.01);


        // 给布隆过滤器添加初始化数据
        List<CategoryEntity> categoryEntities = this.gmallPmsClient.getCategorysByParentId(0l).getData();
        if (!CollectionUtils.isEmpty(categoryEntities)) {
            categoryEntities.forEach(categoryEntity -> {
                bloomFilter.add(KEY_PREFIX + "[" + categoryEntity.getId() + "]");
            });
        }
    }


}
