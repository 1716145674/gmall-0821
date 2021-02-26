package com.atguigu.gmall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.service.AttrService;
import com.atguigu.gmall.pms.vo.AttrValueVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.pms.vo.SkuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Autowired
    private AttrService attrService;

    @Override
    public List<SkuAttrValueEntity> querySkuAttrsBySkuIDAndCategoryId(Long cid, Long skuId) {
        List<AttrEntity> attrEntities = attrService.list(new QueryWrapper<AttrEntity>().eq("category_id", cid).eq("search_type", 1));
        if (CollectionUtils.isEmpty(attrEntities)){
            return null;
        }
        List<Long> attrIds = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());

        List<SkuAttrValueEntity> list = this.list(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId).in("attr_id", attrIds));

        return list;
    }
    @Autowired
    private  SkuAttrValueMapper skuAttrValueMapper;

    @Override
    public List<SaleAttrValueVo> querySkuAttrValuesBySpuId(Long spuId) {
        List<AttrValueVo> attrValueVos = skuAttrValueMapper.querySkuAttrValuesBySpuId(spuId);
        // 以attrId进行分组
        Map<Long, List<AttrValueVo>> map = attrValueVos.stream().collect(Collectors.groupingBy(AttrValueVo::getAttrId));

        //创建一个List<SaleAttrValueVo>
        List<SaleAttrValueVo> saleAttrValueVos=new ArrayList<>();
        map.forEach((attrId,attrs)->{
            SaleAttrValueVo saleAttrValueVo = new SaleAttrValueVo();
            // attrId
            saleAttrValueVo.setAttrId(attrId);
            // attrName
            saleAttrValueVo.setAttrName(attrs.get(0).getAttrName());
            //attrValues
            Set<String> attrValues = attrs.stream().map(AttrValueVo::getAttrValue).collect(Collectors.toSet());
            saleAttrValueVo.setAttrValues(attrValues);
            saleAttrValueVos.add(saleAttrValueVo);
        });

        return saleAttrValueVos;
    }

    @Override
    public String querySkusJsonBySpuId(Long spuId) {
        // [{"sku_id": 3, "attr_values": "暗夜黑,12G,512G"}, {"sku_id": 4, "attr_values": "白天白,12G,512G"}]
        List<Map<String, Object>> skus = this.skuAttrValueMapper.querySkusJsonBySpuId(spuId);
        // 转换成：{'暗夜黑,12G,512G': 3, '白天白,12G,512G': 4}
        Map<String, Long> map = skus.stream().collect(Collectors.toMap(sku -> sku.get("attr_values").toString(), sku -> (Long) sku.get("sku_id")));
        return JSON.toJSONString(map);
    }

}