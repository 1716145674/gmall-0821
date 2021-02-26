package com.atguigu.gmall.cart.async;

import com.atguigu.gmall.cart.mapper.CartMapper;
import com.atguigu.gmall.cart.pojo.Cart;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 为了确保统一异常处理时好操作,所有的异步操作的第一个参数必须是userId
 * 这些操作都是异步执行的,需要开辟新的线程来执行.所以做好能够自己配置线程池.
 * 线程池 可以通过代码配置ThreadPoolExecutor 或者通过配置文件的方式配置.
 * @author zqq
 * @create 2021-02-24 18:42
 */
@Component
public class AsyncMapper {

    @Autowired
    private CartMapper cartMapper;

    @Async
    public void insertCart(String userId,Cart cart) {
        this.cartMapper.insert(cart);
    }
    @Async
    public void updateCart(String userId,Cart cart) {
        this.cartMapper.update(cart,new UpdateWrapper<Cart>().eq("user_id",userId).eq("sku_id",cart.getSkuId()));
    }

    public void deleteCart(String userId, Cart cart) {
        this.cartMapper.delete(new QueryWrapper<Cart>().eq("sku_id",cart.getSkuId()).eq("user_id",userId));
    }
    public void deleteCartBySkuIdAndUserId(String userId, String skuId) {
        this.cartMapper.delete(new QueryWrapper<Cart>().eq("sku_id",skuId).eq("user_id",userId));
    }
}
