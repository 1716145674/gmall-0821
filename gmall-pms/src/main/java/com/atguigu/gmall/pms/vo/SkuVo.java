package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zqq
 * @create 2021-01-20 19:12
 */
@Data
public class SkuVo extends SkuEntity {
    // sku图片
    private List<String>  images;

    private List<SkuAttrValueEntity> saleAttrs;
    // 积分信息
    private BigDecimal growBounds;

    private BigDecimal buyBounds;

    private List<Integer> work;

    //满减信息

    private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private Integer fullAddOther;

    //打折信息

    private Integer fullCount;

    private BigDecimal discount;

    private Integer ladderAddOther;

    //库存信息
    private Integer stock;
}
