package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.vo.SalesVo;
import com.atguigu.gmall.sms.service.SkuFullReductionService;
import com.atguigu.gmall.sms.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SkuBoundsMapper;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsMapper, SkuBoundsEntity> implements SkuBoundsService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuBoundsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageResultVo(page);
    }

    @Autowired
    private SkuFullReductionService skuFullReductionService;
    @Autowired
    private SkuLadderService skuLadderService;

    @Transactional
    @Override
    public void saveSales(SalesVo salesVo) {
        // 保存积分信息
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(salesVo, skuBoundsEntity);
        skuBoundsEntity.setWork(salesVo.getWork().get(3) * (2 << 2) + salesVo.getWork().get(2) * (2 << 1) + salesVo.getWork().get(1) * 2 + salesVo.getWork().get(0));
        baseMapper.insert(skuBoundsEntity);
        //保存满减信息
        skuFullReductionService.saveFullReduction(salesVo);
        //保存打折信息
        skuLadderService.saveladder(salesVo);

    }

    @Override
    public List<ItemSaleVo> queryItemSaleVosBySkuId(Long skuId) {
        List<ItemSaleVo> list = new ArrayList<>();
        // 查询积分信息

        SkuBoundsEntity skuBoundsEntity = this.getOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (skuBoundsEntity!=null){
            ItemSaleVo bounds = new ItemSaleVo();
            bounds.setType("积分");
            bounds.setDesc("送" + skuBoundsEntity.getGrowBounds().toBigInteger() + " 成长积分,送" + skuBoundsEntity.getBuyBounds().toBigInteger() + " 购物积分");
            list.add(bounds);
        }

        // 查询满减信息
        SkuFullReductionEntity reductionEntity = skuFullReductionService.getOne(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (reductionEntity!=null){

            ItemSaleVo reduction = new ItemSaleVo();
            reduction.setType("满减");
            reduction.setDesc("满" + reductionEntity.getFullPrice() + "减" + reductionEntity.getReducePrice());
            list.add(reduction);
        }

        // 查询打折信息
        SkuLadderEntity ladderEntity = skuLadderService.getOne(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if (ladderEntity!=null){
            ItemSaleVo ladder = new ItemSaleVo();
            ladder.setType("打折");
            ladder.setDesc("满" + ladderEntity.getFullCount() + "件打" + ladderEntity.getDiscount().divide(new BigDecimal(10)) + "折");
            list.add(ladder);
        }

        return list;
    }


}