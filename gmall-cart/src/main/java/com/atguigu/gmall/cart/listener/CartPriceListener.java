package com.atguigu.gmall.cart.listener;

import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.rabbitmq.client.Channel;
import io.lettuce.core.Value;
import io.lettuce.core.dynamic.annotation.Key;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author zqq
 * @create 2021-02-25 10:03
 */
@Component
public class CartPriceListener {

    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String RRICE_PROFIX = "cart:price:";

    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = "PMS_GOODS_UPDATE_PRICE_QUEUE" ,durable = "true"),
            exchange = @Exchange(value = "PMS_GOODS_INSERT_EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = "item.updatePrice")})
    public void listenCartPrice(Channel channel, Long skuId, Message message){
        try {
            if (skuId==null){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }

            ResponseVo<SkuEntity> skuEntityResponseVo = pmsClient.querySkuById(skuId);
            SkuEntity skuEntity = skuEntityResponseVo.getData();

            if (skuEntity==null){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }

            this.redisTemplate.opsForValue().set(RRICE_PROFIX+skuId,skuEntity.getPrice().toString());

            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

        } catch (IOException e) {
            try {
                if (message.getMessageProperties().getRedelivered()){
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
                }
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
        }

    }
}
