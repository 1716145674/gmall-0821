package com.atguigu.gmall.item.service;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmalllWmsClient;
import com.atguigu.gmall.item.vo.ItemVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.wms.vo.SkuWareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import rx.Completable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author zqq
 * @create 2021-02-21 15:59
 */
@Service
public class ItemService {

    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallSmsClient smsClient;
    @Autowired
    private GmalllWmsClient wmsClient;

    @Autowired
    private ThreadPoolExecutor poolExecutor;

    public ItemVo load(Long skuId) {
        ItemVo itemVo = new ItemVo();
        // 异步编排
        CompletableFuture<SkuEntity> skuFuture = CompletableFuture.supplyAsync(() -> {
            // 根据skuId查询sku的信息1
            ResponseVo<SkuEntity> skuEntityResponseVo = this.pmsClient.querySkuById(skuId);
            SkuEntity skuEntity = skuEntityResponseVo.getData();
            if (skuEntity == null) {
                return null;
            }
            itemVo.setSkuId(skuId);
            itemVo.setTitle(skuEntity.getTitle());
            itemVo.setSubTitle(skuEntity.getSubtitle());
            itemVo.setPrice(skuEntity.getPrice());
            itemVo.setWeight(skuEntity.getWeight());
            itemVo.setDefaultImage(skuEntity.getDefaultImage());
            return skuEntity;
        }, poolExecutor);

        CompletableFuture<Void> future1 = skuFuture.thenAcceptAsync((skuEntity) -> {
            // 根据cid3查询分类信息2
            ResponseVo<List<CategoryEntity>> categoryResponseVo = this.pmsClient.queryLevel123CategoriesByLevel3Id(skuEntity.getCategoryId());
            List<CategoryEntity> categoryEntities = categoryResponseVo.getData();
            itemVo.setCategories(categoryEntities);
        }, poolExecutor);

        CompletableFuture<Void> future2 = skuFuture.thenAcceptAsync((skuEntity) -> {

            // 根据品牌的id查询品牌3
            ResponseVo<BrandEntity> brandEntityResponseVo = this.pmsClient.queryBrandById(skuEntity.getBrandId());
            BrandEntity brandEntity = brandEntityResponseVo.getData();
            if (brandEntity != null) {
                itemVo.setBrandId(brandEntity.getId());
                itemVo.setBrandName(brandEntity.getName());
            }
        }, poolExecutor);

        CompletableFuture<Void> future3 = skuFuture.thenAcceptAsync((skuEntity) -> {

            // 根据spuId查询spu
            ResponseVo<SpuEntity> spuEntityResponseVo = this.pmsClient.querySpuById(skuEntity.getSpuId());
            SpuEntity spuEntity = spuEntityResponseVo.getData();
            if (spuEntity != null) {
                itemVo.setSpuId(spuEntity.getId());
                itemVo.setSpuName(spuEntity.getName());
            }
        }, poolExecutor);

        CompletableFuture<Void> future4 = CompletableFuture.runAsync(() -> {
            // 跟据skuId查询图片5
            ResponseVo<List<SkuImagesEntity>> skuImagesResponseVo = this.pmsClient.querySkuImagesBySkuId(skuId);
            List<SkuImagesEntity> skuImagesEntities = skuImagesResponseVo.getData();
            itemVo.setImages(skuImagesEntities);
        }, poolExecutor);

        CompletableFuture<Void> future5 = CompletableFuture.runAsync(() -> {
            // 根据skuId查询sku营销信息6
            ResponseVo<List<ItemSaleVo>> salesResponseVo = this.smsClient.queryItemSaleVosBySkuId(skuId);
            List<ItemSaleVo> sales = salesResponseVo.getData();
            if (!CollectionUtils.isEmpty(sales)){
                itemVo.setSales(sales);
            }
        }, poolExecutor);


        CompletableFuture<Void> future6 = CompletableFuture.runAsync(() -> {
            // 根据skuId查询sku的库存信息7
            ResponseVo<SkuWareVo> skuWareVoResponseVo = this.wmsClient.queryStoreAndSalesBySkuId(skuId);
            SkuWareVo skuWareVo = skuWareVoResponseVo.getData();
            if (skuWareVo != null) {
                itemVo.setStore(skuWareVo.getStore());
            }
        }, poolExecutor);

        CompletableFuture<Void> future7 = skuFuture.thenAcceptAsync((skuEntity) -> {
            // 根据spuId查询spu下的所有sku的销售属性8
            ResponseVo<List<SaleAttrValueVo>> saleAttrValueVoResponseVo = this.pmsClient.querySkuAttrValuesBySpuId(skuEntity.getSpuId());
            List<SaleAttrValueVo> saleAttrValueVos = saleAttrValueVoResponseVo.getData();
            itemVo.setSaleAttrs(saleAttrValueVos);
        }, poolExecutor);

        CompletableFuture<Void> future8 = skuFuture.thenAcceptAsync((skuEntity) -> {
            // 当前sku的销售属性9
            ResponseVo<List<SkuAttrValueEntity>> saleAttrResponseVo = this.pmsClient.querySkuAttrSBySkuId(skuId);
            List<SkuAttrValueEntity> skuAttrValueEntities = saleAttrResponseVo.getData();
            if (!CollectionUtils.isEmpty(skuAttrValueEntities)){
                Map<Long, String> map = skuAttrValueEntities.stream().collect(Collectors.toMap(SkuAttrValueEntity::getAttrId, SkuAttrValueEntity::getAttrValue));
                itemVo.setSaleAttr(map);
            }
        }, poolExecutor);

        CompletableFuture<Void> future9 = skuFuture.thenAcceptAsync((skuEntity) -> {
            // 根据spuId查询spu下的所有sku及销售属性的映射关系10
            ResponseVo<String> skusJsonResponseVo = this.pmsClient.querySkusJsonBySpuId(skuEntity.getSpuId());
            String skusJson = skusJsonResponseVo.getData();
            itemVo.setSkusJson(skusJson);
        }, poolExecutor);

        CompletableFuture<Void> future10 = skuFuture.thenAcceptAsync((skuEntity) -> {
            // 根据spuId查询spu的海报信息11
            ResponseVo<List<SpuDescEntity>> listResponseVo = this.pmsClient.querySpuDescById(skuEntity.getSpuId());
            List<SpuDescEntity> spuDescEntityList = listResponseVo.getData();
            if (!CollectionUtils.isEmpty(spuDescEntityList)) {
                List<String> StringList = spuDescEntityList.stream().map(SpuDescEntity::getDecript).collect(Collectors.toList());
                itemVo.setSpuImages(StringList);
            }
        }, poolExecutor);

        CompletableFuture<Void> future11 = skuFuture.thenAcceptAsync((skuEntity) -> {
            // 根据cid3 spuId skuId查询组及组下的规格参数及值 12
            ResponseVo<List<ItemGroupVo>> groupResponseVo = this.pmsClient.queryGroupsBySpuIdAndCid( skuEntity.getSpuId(), skuId,skuEntity.getCategoryId());
            List<ItemGroupVo> itemGroupVos = groupResponseVo.getData();
            itemVo.setGroups(itemGroupVos);
        }, poolExecutor);


        CompletableFuture.allOf(future1,future2,future3,future4,future5,future6,future7,future8,future9,future10,future11).join();

        return itemVo;
    }

    @Autowired
    private TemplateEngine templateEngine;

    private void createHtml(Long skuId){
        ItemVo itemVo = this.load(skuId);
        Context context = new Context();
       context.setVariable("itemVo",itemVo);
        File file = new File("D:\\html");
        if (!file.exists()){
            try {
                if (!file.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try(PrintWriter printWriter=new PrintWriter(file)) {
            this.templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void  asyncExecute(Long skuId){

        poolExecutor.execute(()->{this.createHtml(skuId);});
    }
}
