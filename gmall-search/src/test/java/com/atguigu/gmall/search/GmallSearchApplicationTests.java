package com.atguigu.gmall.search;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.vo.Goods;
import com.atguigu.gmall.search.vo.SearchAttrValue;
import com.atguigu.gmall.wms.vo.SkuWareVo;
import org.elasticsearch.repositories.Repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;
    @Autowired
    private GoodsRepository repository;
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;

    @Test
    void doImport() {

        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);

        Integer pageNum = 1;
        Integer pageSize = 100;
        do {
            //分页查询spu
            PageParamVo pageParamVo = new PageParamVo(pageNum, pageSize, null);
            ResponseVo<List<SpuEntity>> listResponseVo = pmsClient.querySpusByPage(pageParamVo);
            List<SpuEntity> spuEntities = listResponseVo.getData();
            if (CollectionUtils.isEmpty(spuEntities)) {
                return;
            }
            spuEntities.forEach(spuEntity -> {
                List<SkuEntity> skuEntities = pmsClient.querySkusBySpuId(spuEntity.getId()).getData();
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
                        if (skuWareVo!=null){
                            goods.setSales(skuWareVo.getSales());
                            goods.setStore(skuWareVo.getStore());
                        }
                        // 品牌
                        BrandEntity brandEntity = pmsClient.queryBrandById(sku.getBrandId()).getData();
                        if (brandEntity!=null){
                            goods.setBrandId(brandEntity.getId());
                            goods.setBrandName(brandEntity.getName());
                            goods.setLogo(brandEntity.getLogo());
                        }
                        // 分类
                        CategoryEntity categoryEntity = pmsClient.queryCategoryById(sku.getCategoryId()).getData();
                        if (categoryEntity!=null){
                            goods.setCategoryId(categoryEntity.getId());
                            goods.setCategoryName(categoryEntity.getName());
                        }
                        //检索参数
                        List<SearchAttrValue> searchAttrValues =new ArrayList<>();

                        List<SpuAttrValueEntity> spuAttrValueEntities = pmsClient.querySpuAttrsBySpuIDAndCategoryId(sku.getCategoryId(), sku.getSpuId()).getData();

                        if(!CollectionUtils.isEmpty(spuAttrValueEntities)){
                            List<SearchAttrValue> searchAttrValueList = spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
                                SearchAttrValue searchAttrValue = new SearchAttrValue();
                                BeanUtils.copyProperties(spuAttrValueEntity, searchAttrValue);
                                return searchAttrValue;
                            }).collect(Collectors.toList());
                            searchAttrValues.addAll(searchAttrValueList);
                        }

                        List<SkuAttrValueEntity> skuAttrValueEntities = pmsClient.querySkuAttrsBySkuIdAndCategoryId(sku.getCategoryId(), sku.getId()).getData();

                        if (!CollectionUtils.isEmpty(skuAttrValueEntities)){
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
                }

            });

            pageSize = spuEntities.size();
            pageNum++;
        } while (pageSize == 100);
    }

}
