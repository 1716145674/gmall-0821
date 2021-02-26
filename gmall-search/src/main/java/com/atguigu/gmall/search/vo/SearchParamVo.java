package com.atguigu.gmall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zqq
 * @create 2021-01-29 18:08
 */
@Data
public class SearchParamVo {
    private String keyword;// 匹配查询
    private List<Long> brandId; // 品牌id 可以多选
    private List<Long> categoryId; // 分类id目前只有一个,但是为了可用性更高,使用集合
    private List<String> props;//属性每一个字符串代表一个属性查询 格式是 属性id:值-值;
    private Double priceFrom;
    private Double priceTo;
    private Boolean store=false;
    private Integer sort=0;// 排序类型,默认0按照分数排序,1价格升序,2价格降序,3销量降序,4新品降序
    private Integer pageNum=1;
    private final Integer pageSize=20;



}
