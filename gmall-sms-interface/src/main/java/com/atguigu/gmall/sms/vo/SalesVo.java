package com.atguigu.gmall.sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zqq
 * @create 2021-01-20 20:52
 */
@Data
public class SalesVo {
    // skuid
    private Long skuId;
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
}
