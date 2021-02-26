package com.atguigu.gmall.wms.service.impl;

import com.atguigu.gmall.wms.vo.SkuWareVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.wms.mapper.WareSkuMapper;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import org.springframework.util.CollectionUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuMapper, WareSkuEntity> implements WareSkuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<WareSkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public SkuWareVo queryStoreAndSalesBySkuId(Long skuId) {
        SkuWareVo skuWareVo = null;

        List<WareSkuEntity> skuEntityList = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId));
        if (!CollectionUtils.isEmpty(skuEntityList)) {
            skuWareVo=new SkuWareVo();
            //统计销量
            Optional<Long> allSales = skuEntityList.stream().map(WareSkuEntity::getSales).reduce((a, b) -> a + b);
            skuWareVo.setSales(allSales.get());
            //判断是否有货
            boolean store = skuEntityList.stream().anyMatch(wareSkuEntity ->
                    wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0
            );
            skuWareVo.setStore(store);
        }
        return skuWareVo;
    }

}