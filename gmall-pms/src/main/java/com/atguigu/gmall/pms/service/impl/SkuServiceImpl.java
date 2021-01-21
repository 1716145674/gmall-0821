package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.atguigu.gmall.sms.api.vo.SalesVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.service.SkuService;
import org.springframework.util.CollectionUtils;


@Service("skuService")
public class SkuServiceImpl extends ServiceImpl<SkuMapper, SkuEntity> implements SkuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuEntity>()
        );

        return new PageResultVo(page);
    }

    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuAttrValueService skuAttrValueService;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Override
    public void saveSkus(SpuVo spu, Long spuId, Long spuBrandId, Long spuCategoryId) {
        List<SkuVo> skus = spu.getSkus();
        if (!CollectionUtils.isEmpty(skus)){
            for (SkuVo skuVo : skus) {
                SkuEntity skuEntity = new SkuEntity();
                BeanUtils.copyProperties(skuVo,skuEntity);
                skuEntity.setSpuId(spuId);
                skuEntity.setBrandId(spuBrandId);
                skuEntity.setCategoryId(spuCategoryId);
                skuEntity.setDefaultImage(StringUtils.isBlank(skuVo.getDefaultImage())?skuVo.getImages().get(0):skuVo.getDefaultImage());
                if (StringUtils.isBlank(skuVo.getDefaultImage())){
                    skuVo.setDefaultImage(skuVo.getImages().get(0));
                }
                this.save(skuEntity);
                Long skuId = skuEntity.getId();
                // 保存sku_images
                skuImagesService.saveSkuimages(skuVo, skuId);

                // 保存sku_attr_value
                skuAttrValueService.saveSkuAttrValue(skuVo, skuId);

                // 3.远程调用保存sms相关信息
                SalesVo salesVo = new SalesVo();
                BeanUtils.copyProperties(skuVo,salesVo);
                salesVo.setSkuId(skuId);
                gmallSmsClient.saveSales(salesVo);
            }
        }
    }



}