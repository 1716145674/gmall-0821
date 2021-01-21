package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.vo.SkuVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SkuImagesMapper;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.service.SkuImagesService;
import org.springframework.util.CollectionUtils;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesMapper, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuImagesEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public void saveSkuimages(SkuVo skuVo, Long skuId) {
        List<String> skuVoImages = skuVo.getImages();
        if (!CollectionUtils.isEmpty(skuVoImages)){
            this.saveBatch(skuVoImages.stream().map(image->{
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setUrl(image);
                skuImagesEntity.setDefaultStatus(StringUtils.equals(image,skuVo.getDefaultImage())?1:0);
                return skuImagesEntity;
            }).collect(Collectors.toList()));
        }
    }

}