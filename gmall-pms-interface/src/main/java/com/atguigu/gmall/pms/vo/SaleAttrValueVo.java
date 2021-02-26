package com.atguigu.gmall.pms.vo;

import lombok.Data;

import java.util.Set;

/**
 * @author zqq
 * @create 2021-02-19 18:56
 */
@Data
public class SaleAttrValueVo {
    private Long attrId;//销售属性的Id
    private String attrName;// 销售属行名字
    private Set<String> attrValues;//销售属性的值
}
