package com.atguigu.gmall.search.listener;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.vo.Goods;
import com.atguigu.gmall.search.vo.SearchAttrValue;
import com.atguigu.gmall.wms.vo.SkuWareVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zqq
 * @create 2021-02-02 19:18
 */
@Configuration
public class GoodListener {

    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private GoodsRepository repository;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "PMS_GOODS_INSERT_QUEUE", durable = "true"),
                    exchange = @Exchange(value = "PMS_GOODS_INSERT_EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
                    key = "item.insert"
            )

    )
    public void listen(Message message, Channel channel, Long spuId) {
        try {
            if (spuId == null) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            SpuEntity spuEntity = pmsClient.querySpuById(spuId).getData();
            if (spuEntity == null) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            List<SkuEntity> skuEntities = pmsClient.querySkusBySpuId(spuId).getData();
            if (!CollectionUtils.isEmpty(skuEntities)) {
                List<Goods> goodsList = skuEntities.stream().map(sku -> {
                    Goods goods = new Goods();
                    // 创建时间
                    goods.setCreateTime(spuEntity.getCreateTime());
                    // sku相关信息
                    goods.setSkuId(sku.getId());
                    goods.setTitle(sku.getTitle());
                    goods.setSubTitle(sku.getSubtitle());
                    goods.setPrice(sku.getPrice().doubleValue());
                    goods.setDefaultImage(sku.getDefaultImage());
                    // 销量和是否有货
                    SkuWareVo skuWareVo = wmsClient.queryStoreAndSalesBySkuId(sku.getId()).getData();
                    if (skuWareVo != null) {
                        goods.setSales(skuWareVo.getSales());
                        goods.setStore(skuWareVo.getStore());
                    }
                    // 品牌
                    BrandEntity brandEntity = pmsClient.queryBrandById(sku.getBrandId()).getData();
                    if (brandEntity != null) {
                        goods.setBrandId(brandEntity.getId());
                        goods.setBrandName(brandEntity.getName());
                        goods.setLogo(brandEntity.getLogo());
                    }
                    // 分类
                    CategoryEntity categoryEntity = pmsClient.queryCategoryById(sku.getCategoryId()).getData();
                    if (categoryEntity != null) {
                        goods.setCategoryId(categoryEntity.getId());
                        goods.setCategoryName(categoryEntity.getName());
                    }
                    //检索参数
                    List<SearchAttrValue> searchAttrValues = new ArrayList<>();

                    List<SpuAttrValueEntity> spuAttrValueEntities = pmsClient.querySpuAttrsBySpuIDAndCategoryId(sku.getCategoryId(), sku.getSpuId()).getData();

                    if (!CollectionUtils.isEmpty(spuAttrValueEntities)) {
                        List<SearchAttrValue> searchAttrValueList = spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
                            SearchAttrValue searchAttrValue = new SearchAttrValue();
                            BeanUtils.copyProperties(spuAttrValueEntity, searchAttrValue);
                            return searchAttrValue;
                        }).collect(Collectors.toList());
                        searchAttrValues.addAll(searchAttrValueList);
                    }

                    List<SkuAttrValueEntity> skuAttrValueEntities = pmsClient.querySkuAttrsBySkuIdAndCategoryId(sku.getCategoryId(), sku.getId()).getData();

                    if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                        List<SearchAttrValue> searchAttrValues1 = skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
                            SearchAttrValue searchAttrValue = new SearchAttrValue();
                            BeanUtils.copyProperties(skuAttrValueEntity, searchAttrValue);
                            return searchAttrValue;
                        }).collect(Collectors.toList());
                        searchAttrValues.addAll(searchAttrValues1);
                    }

                    goods.setSearchAttrs(searchAttrValues);
                    return goods;
                }).collect(Collectors.toList());
                repository.saveAll(goodsList);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
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