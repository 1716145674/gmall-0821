package com.atguigu.gmall.sms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.vo.SalesVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author zqq
 * @create 2021-01-20 20:50
 */

public interface GmallSmsApi {
    // 保存sku的销售信息
    @PostMapping("sms/skubounds/saveSales")
    public ResponseVo saveSales(@RequestBody SalesVo salesVo);

    //根据skuid查询所有的营销信息
    @GetMapping("sms/skubounds/sales/{skuId}")
    public ResponseVo<List<ItemSaleVo>> queryItemSaleVosBySkuId(@PathVariable("skuId") Long skuId);

}
