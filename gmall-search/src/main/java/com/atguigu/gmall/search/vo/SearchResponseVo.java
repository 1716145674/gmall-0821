package com.atguigu.gmall.search.vo;

import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author zqq
 * @create 2021-01-29 18:37
 */
@Data
public class SearchResponseVo {
    private List<BrandEntity> brands;
    private List<CategoryEntity> categories;
    private List<SearchResponseAttrVo> filters;
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private List<Goods> goodsList;



}
