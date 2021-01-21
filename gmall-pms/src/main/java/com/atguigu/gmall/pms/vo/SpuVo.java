package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuEntity;
import lombok.Data;

import java.util.List;

/**
 * @author zqq
 * @create 2021-01-20 18:46
 */
@Data
public class SpuVo extends SpuEntity {

    // spu图片
    private List<String> spuImages;

    // spu基本属性
    private List<SpuAttrValuesVo> baseAttrs;

    // sku
    private List<SkuVo> skus;
}
