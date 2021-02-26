package com.atguigu.gmall.cart.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.async.AsyncMapper;
import com.atguigu.gmall.cart.config.CartInterceptor;
import com.atguigu.gmall.cart.exception.CartException;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.feign.GmallSmsClient;
import com.atguigu.gmall.cart.feign.GmalllWmsClient;
import com.atguigu.gmall.cart.mapper.CartMapper;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.pojo.UserInfo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.sms.api.GmallSmsApi;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.wms.vo.SkuWareVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.awt.geom.AreaOp;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zqq
 * @create 2021-02-24 10:26
 */
@Service
public class CartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private AsyncMapper asyncMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmalllWmsClient wmsClient;
    @Autowired
    private GmallSmsClient smsClient;

    private static final String KEY_PROFIX = "cart:info:";
    private static final String RRICE_PROFIX = "cart:price:";

    public void addCart(Cart cart) {
        Long skuId = cart.getSkuId();
        BigDecimal count = cart.getCount();
        String tojson = null;
        //1.先拿到用户的登录信息
        String userId = getUserId();

        cart.setUserId(userId);

        //3.拿到skuId 封装 商品信息
        ResponseVo<SkuEntity> skuEntityResponseVo = pmsClient.querySkuById(skuId);
        SkuEntity skuEntity = skuEntityResponseVo.getData();
        if (skuEntity == null) {
            throw new CartException("当前商品不存在");
        }
        cart.setTitle(skuEntity.getTitle());
        cart.setCheck(true);
        cart.setDefaultImage(skuEntity.getDefaultImage());
        cart.setPrice(skuEntity.getPrice());

        //拿到商品的价格加入到缓存中做商品价格同步
        this.redisTemplate.opsForValue().set(RRICE_PROFIX + skuId, skuEntity.getPrice().toString());

        ResponseVo<List<SkuAttrValueEntity>> listResponseVo = pmsClient.querySkuAttrSBySkuId(skuId);
        List<SkuAttrValueEntity> skuAttrValueEntities = listResponseVo.getData();
        if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
            tojson = JSON.toJSON(skuAttrValueEntities).toString();
            cart.setSaleAttrs(tojson);
        }

        ResponseVo<SkuWareVo> skuWareVoResponseVo = wmsClient.queryStoreAndSalesBySkuId(skuId);
        SkuWareVo skuWareVo = skuWareVoResponseVo.getData();
        if (skuWareVo != null) {
            cart.setStore(skuWareVo.getStore());
        }

        ResponseVo<List<ItemSaleVo>> listResponseVo1 = smsClient.queryItemSaleVosBySkuId(skuId);
        List<ItemSaleVo> itemSaleVos = listResponseVo1.getData();
        if (!CollectionUtils.isEmpty(itemSaleVos)) {
            tojson = JSON.toJSON(itemSaleVos).toString();
            cart.setSales(tojson);
        }

        //3.拿到操作当前购物车的hash的map对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(KEY_PROFIX + userId);
        // 如果当前redis中没有此数据 插入
        if (!hashOps.hasKey(skuId.toString())) {
            // 异步添加mysql
            this.asyncMapper.insertCart(userId,cart);
        } else {
            // 如果当前redis中有此数据 跟新
            cart = JSON.parseObject(hashOps.get(skuId.toString()).toString(), Cart.class);
            cart.setCount(cart.getCount().add(count));
            //异步更新mysql

            this.asyncMapper.updateCart(userId,cart);
            //
        }
        hashOps.put(skuId.toString(), JSON.toJSON(cart).toString());


    }

    public Cart queryCartBySkuId(Long skuId) {
        //1.先拿到用户的登录信息
        String userId = getUserId();
        return this.cartMapper.selectOne(new QueryWrapper<Cart>().eq("sku_id", skuId).eq("user_id", userId));
    }

    public List<Cart> queryCarts() {
        // 1.先拿到未登录状态下的购物车
        UserInfo userInfo = CartInterceptor.getUserInfo();
        String userKey = userInfo.getUserKey();
        Long userId = userInfo.getUserId();

        BoundHashOperations<String, Object, Object> unLoginHashOps = redisTemplate.boundHashOps(KEY_PROFIX + userKey);


        List<Cart> carts = null;

        // 遍历所有的数据转化为cart集合
        List<Object> values = unLoginHashOps.values();
        if (!CollectionUtils.isEmpty(values)) {
            carts = values.stream().map(o -> {
                Cart cart = JSON.parseObject(o.toString(), Cart.class);
                // 获取到redis中缓存的价格进行同步
                String currentPrice = this.redisTemplate.opsForValue().get(RRICE_PROFIX+cart.getSkuId().toString());
                if (StringUtils.isNotBlank(currentPrice)){
                    cart.setCurrentPrice(new BigDecimal(currentPrice));
                }
                return cart;
            }).collect(Collectors.toList());

        }
        // 2.如果在未登录下查询则直接返回
        if (userId == null) {
            return carts;
        }
        // 3. 如果在登录状态下,查看未登录的购物车和已有购物车的关系
        BoundHashOperations<String, Object, Object> loginHashOps = redisTemplate.boundHashOps(KEY_PROFIX + userId);
        // 如果未登录的购物车有数据
        if (!CollectionUtils.isEmpty(carts)) {
            carts.forEach(cart -> {
                BigDecimal count = cart.getCount();
                // 设置当前cart的id为登录了的用户的id
                cart.setUserId(userId.toString());

                // 如果当前cart在登录的购物车中的话
                if (loginHashOps.hasKey(cart.getSkuId().toString())) {
                    cart = JSON.parseObject(loginHashOps.get(cart.getSkuId().toString()).toString(), Cart.class);
                    cart.setCount(cart.getCount().add(count));
                    // 异步跟新
                    this.asyncMapper.updateCart(userId.toString(),cart);
                } else {
                    this.asyncMapper.insertCart(userId.toString(),cart);
                }

                // 否则做新增
                loginHashOps.put(cart.getSkuId().toString(), JSON.toJSON(cart).toString());

                // 合并完毕后删除未登录的购物车
                this.redisTemplate.delete(KEY_PROFIX + userKey);
                this.asyncMapper.deleteCart(userKey, cart);
            });
        }
        // 如果未登录的购物车没有数据 直接返回已登录购物车
        List<Object> loginCartJsons = loginHashOps.values();
        if (!CollectionUtils.isEmpty(loginCartJsons)) {
            return loginCartJsons.stream().map(loginCartJson -> {

                        Cart cart = JSON.parseObject(loginCartJson.toString(), Cart.class);
                        // 获取到redis中缓存的价格进行同步
                        String currentPrice = this.redisTemplate.opsForValue().get(RRICE_PROFIX+cart.getSkuId().toString());
                        cart.setCurrentPrice(new BigDecimal(currentPrice));
                        return cart;

                    }
            ).collect(Collectors.toList());
        }


        return null;
    }

    public void updateNum(Cart cart) {
        if (cart.getSkuId()==null){
            return ;
        }
        BigDecimal count = cart.getCount();
        String userId = getUserId();
        //3.拿到操作当前购物车的hash的map对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(KEY_PROFIX + userId);
        String skuId = cart.getSkuId().toString();
        if (hashOps.hasKey(skuId)) {
             cart = JSON.parseObject(hashOps.get(skuId).toString(), Cart.class);
             cart.setCount(count);
             hashOps.put(skuId,JSON.toJSON(cart).toString());
             // 异步跟新数据库
            this.asyncMapper.updateCart(userId,cart);
        }

    }

    public String getUserId() {
        //1.先拿到用户的登录信息
        UserInfo userInfo = CartInterceptor.getUserInfo();
        //2. 拿到购物车的key
        Long user = userInfo.getUserId();
        String userId = "";
        if (user == null) {
            userId = userInfo.getUserKey();
        } else {
            userId = String.valueOf(user);
        }
        return userId;
    }

    public void deleteCart(Long skuId) {
        String userId = getUserId();
        //3.拿到操作当前购物车的hash的map对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(KEY_PROFIX + userId);
        if (hashOps.hasKey(skuId.toString())) {

            hashOps.delete(skuId.toString());
            // 异步跟新数据库
            this.asyncMapper.deleteCartBySkuIdAndUserId(userId,skuId.toString());
        }
    }
}
