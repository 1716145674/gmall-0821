package com.atguigu.gmall.scheduling.scheduled;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.scheduling.feign.GmallPmsClient;
import com.atguigu.gmall.scheduling.mapper.CartMapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zqq
 * @create 2021-02-26 18:40
 */
@Component
public class ScheduledHandler {
    public static final String KEY_PREFIX = "index:categories:";
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final  String CART_ASYNC_EXCEPTION="cart:asyncException";
    private static final String KEY_PROFIX = "cart:info:";
    @Autowired
    private CartMapper cartMapper;

    @XxlJob("cartDataSyncHandler")
    public ReturnT<String> cartDataSync(String param){
        //1.拿到所有购物车的异常数据
        BoundSetOperations<String, String> setOps = this.redisTemplate.boundSetOps(CART_ASYNC_EXCEPTION);
        if (setOps.size()==0){
            return ReturnT.SUCCESS;
        }
        //2. 遍历数据同步到数据库中
        Set<String> userIds = setOps.members();
        userIds.forEach(userId->{
            // 删除数据库中的记录
            this.cartMapper.delete(new UpdateWrapper<Cart>().eq("user_id",userId));
            //拿到缓存中的数据
            BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PROFIX + userId);
            if (hashOps.size()!=0) {
                hashOps.values().forEach(o->{
                    this.cartMapper.insert( JSON.parseObject(o.toString(), Cart.class));
                });
            }

        });

        return ReturnT.SUCCESS;
    }
    @XxlJob("indexBloomFilterHandler")
    public ReturnT<String> indexBloomFilter(String param){
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

        return ReturnT.SUCCESS;
    }
}
