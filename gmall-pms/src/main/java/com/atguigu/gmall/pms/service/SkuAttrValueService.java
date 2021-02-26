package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author zqq
 * @email zqq@atguigu.com
 * @date 2021-01-18 21:05:27
 */
public interface SkuAttrValueService extends IService<SkuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    void saveSkuAttrValue(SkuVo skuVo, Long skuId);

    List<SkuAttrValueEntity> querySkuAttrsBySkuIDAndCategoryId(Long cid, Long skuId);

    List<SaleAttrValueVo> querySkuAttrValuesBySpuId(Long spuId);

    String querySkusJsonBySpuId(Long spuId);
}

