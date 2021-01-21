package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.vo.SkuVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import org.springframework.util.CollectionUtils;


@Service("skuAttrValueService")
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValueEntity> implements SkuAttrValueService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public void saveSkuAttrValue(SkuVo skuVo, Long skuId) {
        List<SkuAttrValueEntity> skuAttrValueEntities = skuVo.getSaleAttrs();
        if (!CollectionUtils.isEmpty(skuAttrValueEntities)){
            this.saveBatch(skuAttrValueEntities.stream().map(skuAttrValueEntity->{
                skuAttrValueEntity.setSkuId(skuId);
                return skuAttrValueEntity;
            }).collect(Collectors.toList()));
        }
    }

}