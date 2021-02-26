package com.atguigu.gmall.wms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.wms.vo.SkuWareVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zqq
 * @create 2021-01-28 12:56
 */
public interface GamllWmsApi {
    // 根据sku_id查询是否有货,以及销量信息
    @GetMapping("wms/waresku/search/{skuId}")
     ResponseVo<SkuWareVo> queryStoreAndSalesBySkuId(@PathVariable("skuId") Long skuId);
}
