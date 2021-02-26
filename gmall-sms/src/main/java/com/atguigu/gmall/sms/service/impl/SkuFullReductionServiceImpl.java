package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.vo.SalesVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SkuFullReductionMapper;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionMapper, SkuFullReductionEntity> implements SkuFullReductionService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuFullReductionEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public void saveFullReduction(SalesVo salesVo) {
        SkuFullReductionEntity skuFullReductionEntity=new SkuFullReductionEntity();
        BeanUtils.copyProperties(salesVo,skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(salesVo.getFullAddOther());
        this.save(skuFullReductionEntity);
    }

}