package com.atguigu.gmall.wms.service;

import com.atguigu.gmall.wms.vo.SkuWareVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author oono
 * @email andychao3210@gmail.com
 * @date 2021-01-21 10:52:15
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    SkuWareVo queryStoreAndSalesBySkuId(Long skuId);
}

